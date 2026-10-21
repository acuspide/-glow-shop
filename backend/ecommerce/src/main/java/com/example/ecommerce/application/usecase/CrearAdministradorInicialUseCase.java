package com.example.ecommerce.application.usecase;

import com.example.ecommerce.application.PasswordHasher;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.ContrasenaPlana;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.FechaNacimiento;
import com.example.ecommerce.domain.valueobject.Telefono;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Siembra el primer administrador al arrancar la aplicación.
 * Es idempotente: si el correo ya existe, no hace nada.
 */
@Service
@RequiredArgsConstructor
public class CrearAdministradorInicialUseCase {

    private final UsuarioRepository repository;
    private final PasswordHasher passwordHasher;

    /** @return true si lo creó; false si ya existía un usuario con ese correo. */
    @Transactional
    public boolean ejecutar(String id, String nombre, String email, String contrasenaPlano,
                            String telefono, LocalDate fechaNacimiento) {
        Email correo = new Email(email);

        // Si ya existe un usuario con ese correo no hace nada (puede arrancar muchas veces).
        return repository.buscarPorEmail(correo)
                .map(existente -> false)
                .orElseGet(() -> crear(id, nombre, correo, contrasenaPlano, telefono, fechaNacimiento));
    }

    private boolean crear(String id, String nombre, Email correo, String contrasenaPlano,
                          String telefono, LocalDate fechaNacimiento) {
        ContrasenaPlana contrasena = new ContrasenaPlana(contrasenaPlano);
        String contrasenaHash = passwordHasher.hash(contrasena.valor());
        Usuario administrador = Usuario.administradorInicial(
                id, nombre, correo, contrasenaHash,
                new Telefono(telefono), new FechaNacimiento(fechaNacimiento));

        repository.guardar(administrador);
        return true;
    }
}
