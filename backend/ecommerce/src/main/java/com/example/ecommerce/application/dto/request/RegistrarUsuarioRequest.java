package com.example.ecommerce.application.dto.request;

import com.example.ecommerce.domain.valueobject.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegistrarUsuarioRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "El correo electrónico no tiene un formato válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String contrasena,

        // Opcional: si no se envía, el dominio asigna CLIENTE (RN03)
        RolUsuario rol
) {
}
