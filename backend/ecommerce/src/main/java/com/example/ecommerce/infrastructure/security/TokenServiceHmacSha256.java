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
                + ",\"email\":\"" + usuario.getEmail().getValor() + "\""
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
        String[] partes = partesDelToken(token);

        // 1) La firma debe coincidir (comparación en tiempo constante).
        byte[] esperada = firmar(partes[0] + "." + partes[1]);
        if (!MessageDigest.isEqual(esperada, decodificar(partes[2]))) {
            throw new TokenInvalidoException();
        }

        // 2) El header debe ser exactamente el que emitimos (evita "alg":"none").
        if (!HEADER_JSON.equals(texto(partes[0]))) {
            throw new TokenInvalidoException();
        }

        // 3) El payload debe traer sus campos y el token no debe haber expirado.
        String payload = texto(partes[1]);
        String jti = campo(JTI, payload);
        String usuarioId = campo(SUB, payload);
        long expira = numero(campo(EXP, payload));
        if (Instant.now().getEpochSecond() >= expira) {
            throw new TokenInvalidoException();
        }

        // 4) El token no debe haber sido cerrado (logout).
        if (revocados.containsKey(jti)) {
            throw new TokenInvalidoException();
        }
        return new PayloadValido(jti, usuarioId, expira);
    }

    // Un JWT son tres partes separadas por punto: header.payload.firma
    private String[] partesDelToken(String token) {
        if (token == null) {
            throw new TokenInvalidoException();
        }
        String[] partes = token.split("\\.", -1);
        if (partes.length != 3) {
            throw new TokenInvalidoException();
        }
        return partes;
    }

    // El JDK lanza IllegalArgumentException si el texto no es Base64 válido: se traduce a 401.
    private byte[] decodificar(String parte) {
        try {
            return DECODIFICADOR.decode(parte);
        } catch (IllegalArgumentException e) {
            throw new TokenInvalidoException();
        }
    }

    private String texto(String parte) {
        return new String(decodificar(parte), StandardCharsets.UTF_8);
    }

    private String campo(Pattern patron, String payload) {
        Matcher coincidencia = patron.matcher(payload);
        if (!coincidencia.find()) {
            throw new TokenInvalidoException();
        }
        return coincidencia.group(1);
    }

    // El "exp" es un número de segundos; si no cabe en un long, el token no es válido.
    private long numero(String valor) {
        try {
            return Long.parseLong(valor);
        } catch (NumberFormatException e) {
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
