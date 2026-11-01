package com.example.ecommerce.infrastructure.security;

import com.example.ecommerce.application.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Guarda las contraseñas con BCrypt: cada hash lleva su propia sal aleatoria y un costo
 * (número de rondas) que lo hace lento a propósito, para que adivinar contraseñas por
 * fuerza bruta sea muy costoso. Solo usa la librería spring-security-crypto, no los
 * filtros de Spring Security.
 */
@Component
public class PasswordHasherBCrypt implements PasswordHasher {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hash(String contrasenaPlano) {
        return encoder.encode(contrasenaPlano);
    }

    @Override
    public boolean verificar(String contrasenaPlano, String hashAlmacenado) {
        return hashAlmacenado != null && encoder.matches(contrasenaPlano, hashAlmacenado);
    }
}
