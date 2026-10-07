package com.example.ecommerce.application.usecase;

import com.example.ecommerce.application.PasswordHasher;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.CorreoElectronicoDuplicadoException;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.ContrasenaPlana;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.RolUsuario;

public class RegistrarUsuarioUseCase {

    private final UsuarioRepository repository;
    private final PasswordHasher passwordHasher;

    public RegistrarUsuarioUseCase(UsuarioRepository repository, PasswordHasher passwordHasher) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
    }

    public Usuario ejecutar(
            long id,
            String nombre,
            String email,
            String contrasenaPlano,
            RolUsuario rol) {

        ContrasenaPlana contrasena = new ContrasenaPlana(contrasenaPlano); // el dominio valida
        Email correo = new Email(email);                                   // el dominio valida

        // RN01: la unicidad requiere consultar el repositorio, por eso se pregunta aquí.
        if (repository.existePorEmail(correo)) {
            throw new CorreoElectronicoDuplicadoException();
        }

        String contrasenaHash = passwordHasher.hash(contrasena.valor());

        // RN03: el rol por defecto lo decide el dominio.
        Usuario usuario = Usuario.registrar(id, nombre, correo, contrasenaHash, rol);

        repository.guardar(usuario);

        return usuario;
    }
}
