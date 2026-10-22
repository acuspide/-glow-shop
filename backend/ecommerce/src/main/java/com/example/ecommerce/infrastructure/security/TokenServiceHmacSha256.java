package com.example.ecommerce.infrastructure.security;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.TokenInvalidoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JWT firmado con HS256 usando solo el JDK. Formato: header.payload.firma (base64url).
 */
@Component
public class TokenServiceHmacSha256 implements TokenService {

    private static final String HEADER_JSON = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private static final Pattern JTI = Pattern.compile("\"jti\":\"([^\"]+)\"");
    private static final Pattern SUB = Pattern.compile("\"sub\":\"([^\"]+)\"");
    private static final Pattern EXP = Pattern.compile("\"exp\":(\\d+)");
    private static final Base64.Encoder CODIFICADOR = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODIFICADOR = Base64.getUrlDecoder();

    private final byte[] secreto;
    private final long expiracionSegundos;

    // Tokens cerrados antes de expirar: id del token (jti) -> segundo en que expira.
    // Vive en memoria: al reiniciar la aplicación se pierde, pero esos tokens expiran solos.
    private final Map<String, Long> revocados = new ConcurrentHashMap<>();

    public TokenServiceHmacSha256(
            @Value("${glow.jwt.secret}") String secreto,
            @Value("${glow.jwt.expiracion-minutos}") long expiracionMinutos) {
        byte[] bytes = secreto == null ? new byte[0] : secreto.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalArgumentException("El secreto JWT debe tener al menos 32 caracteres.");
        }
        this.secreto = bytes;
        this.expiracionSegundos = expiracionMinutos * 60;
    }

    @Override
    public String generar(Usuario usuario) {
        long emitido = Instant.now().getEpochSecond();
        String payloadJson = "{\"jti\":\"" + UUID.randomUUID() + "\""
                + ",\"sub\":\"" + usuario.getId() + "\""
                + ",\"rol\":\"" + usuario.getRol().name() + "\""
                + ",\"iat\":" + emitido
                + ",\"exp\":" + (emitido + expiracionSegundos) + "}";

        String contenido = codificar(HEADER_JSON) + "." + codificar(payloadJson);
        return contenido + "." + CODIFICADOR.encodeToString(firmar(contenido));
    }

    @Override
    public String extraerUsuarioId(String token) {
        return leerPayloadValido(token).usuarioId();
    }

    @Override
    public void revocar(String token) {
        PayloadValido payload = leerPayloadValido(token);
        revocados.put(payload.jti(), payload.expira());
        // Limpieza: los tokens ya expirados no necesitan seguir en la lista.
        long ahora = Instant.now().getEpochSecond();
        revocados.values().removeIf(expira -> expira <= ahora);
    }

    private record PayloadValido(String jti, String usuarioId, long expira) {
    }

    private PayloadValido leerPayloadValido(String token) {
        if (token == null) {
            throw new TokenInvalidoException();
        }
        String[] partes = token.split("\\.", -1);
        if (partes.length != 3) {
            throw new TokenInvalidoException();
        }

        try {
            // 1) La firma debe coincidir (comparación en tiempo constante).
            byte[] esperada = firmar(partes[0] + "." + partes[1]);
            byte[] recibida = DECODIFICADOR.decode(partes[2]);
            if (!MessageDigest.isEqual(esperada, recibida)) {
                throw new TokenInvalidoException();
            }

            // 2) El header debe ser exactamente el que emitimos (evita "alg":"none").
            if (!HEADER_JSON.equals(new String(DECODIFICADOR.decode(partes[0]), StandardCharsets.UTF_8))) {
                throw new TokenInvalidoException();
            }

            // 3) El token no debe haber expirado.
            String payload = new String(DECODIFICADOR.decode(partes[1]), StandardCharsets.UTF_8);
            Matcher jti = JTI.matcher(payload);
            Matcher exp = EXP.matcher(payload);
            Matcher sub = SUB.matcher(payload);
            if (!jti.find() || !exp.find() || !sub.find()) {
                throw new TokenInvalidoException();
            }
            long expira = Long.parseLong(exp.group(1));
            if (Instant.now().getEpochSecond() >= expira) {
                throw new TokenInvalidoException();
            }

            // 4) El token no debe haber sido cerrado (logout).
            if (revocados.containsKey(jti.group(1))) {
                throw new TokenInvalidoException();
            }
            return new PayloadValido(jti.group(1), sub.group(1), expira);
        } catch (IllegalArgumentException e) { // base64 o número mal formado
            throw new TokenInvalidoException();
        }
    }

    private String codificar(String texto) {
        return CODIFICADOR.encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }

    private byte[] firmar(String contenido) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secreto, "HmacSHA256"));
            return mac.doFinal(contenido.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo firmar el token", e);
        }
    }
}
