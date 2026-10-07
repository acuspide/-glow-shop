package com.example.ecommerce.application;

import com.example.ecommerce.domain.entity.Usuario;

/** Resultado de iniciar sesión: el token y el usuario autenticado. */
public record SesionIniciada(String token, Usuario usuario) {
}
