package com.example.ecommerce.application.usecase;

import com.example.ecommerce.application.PasswordHasher;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.ContrasenaPlana;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.FechaNacimiento;
import com.example.ecommerce.domain.valueobject.Telefono;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Siembra el primer administrador al arrancar la aplicación.
 * Es idempotente: si el correo ya existe, no hace nada.
 */
@Service
public class CrearAdministradorInicialUseCase {

    private final UsuarioRepository repository;
    private final PasswordHasher passwordHasher;

    public CrearAdministradorInicialUseCase(UsuarioRepository repository, PasswordHasher passwordHasher) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
    }

    /** @return true si lo creó; false si ya existía un usuario con ese correo. */
    public boolean ejecutar(long id, String nombre, String email, String contrasenaPlano,
                            String telefono, LocalDate fechaNacimiento) {
        Email correo = new Email(email);
        if (repository.existePorEmail(correo)) {
            return false;
        }

        ContrasenaPlana contrasena = new ContrasenaPlana(contrasenaPlano);
        String contrasenaHash = passwordHasher.hash(contrasena.valor());
        Usuario administrador = Usuario.administradorInicial(
                id, nombre, correo, contrasenaHash,
                new Telefono(telefono), new FechaNacimiento(fechaNacimiento));

        repository.guardar(administrador);
        return true;
    }
}
