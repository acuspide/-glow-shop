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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JWT firmado con HS256 usando solo el JDK. Formato: header.payload.firma (base64url).
 */
@Component
public class TokenServiceHmacSha256 implements TokenService {

    private static final String HEADER_JSON = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private static final Pattern SUB = Pattern.compile("\"sub\":\"(\\d+)\"");
    private static final Pattern EXP = Pattern.compile("\"exp\":(\\d+)");
    private static final Base64.Encoder CODIFICADOR = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODIFICADOR = Base64.getUrlDecoder();

    private final byte[] secreto;
    private final long expiracionSegundos;

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
        String payloadJson = "{\"sub\":\"" + usuario.getId() + "\""
                + ",\"rol\":\"" + usuario.getRol().name() + "\""
                + ",\"iat\":" + emitido
                + ",\"exp\":" + (emitido + expiracionSegundos) + "}";

        String contenido = codificar(HEADER_JSON) + "." + codificar(payloadJson);
        return contenido + "." + CODIFICADOR.encodeToString(firmar(contenido));
    }

    @Override
    public long extraerUsuarioId(String token) {
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
            Matcher exp = EXP.matcher(payload);
            Matcher sub = SUB.matcher(payload);
            if (!exp.find() || !sub.find()) {
                throw new TokenInvalidoException();
            }
            if (Instant.now().getEpochSecond() >= Long.parseLong(exp.group(1))) {
                throw new TokenInvalidoException();
            }
            return Long.parseLong(sub.group(1));
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
