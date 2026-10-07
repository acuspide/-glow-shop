package com.example.ecommerce.application.usecase;

import com.example.ecommerce.application.PasswordHasher;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.CorreoElectronicoDuplicadoException;
import com.example.ecommerce.domain.exception.CreacionUsuarioNoPermitidaException;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.ContrasenaPlana;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.RolUsuario;
import org.springframework.stereotype.Service;

/**
 * Un administrador crea a otro administrador o a un vendedor.
 * Quién puede hacerlo lo decide el dominio (Usuario.crearUsuarioConRol).
 */
@Service
public class CrearUsuarioPorAdministradorUseCase {

    private final UsuarioRepository repository;
    private final PasswordHasher passwordHasher;

    public CrearUsuarioPorAdministradorUseCase(UsuarioRepository repository, PasswordHasher passwordHasher) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
    }

    public Usuario ejecutar(
            long administradorId,
            long id,
            String nombre,
            String email,
            String contrasenaPlano,
            RolUsuario rol) {

        // Si quien pide no existe, tampoco tiene permiso.
        Usuario administrador = repository.obtenerPorId(administradorId)
                .orElseThrow(CreacionUsuarioNoPermitidaException::new);

        ContrasenaPlana contrasena = new ContrasenaPlana(contrasenaPlano);
        Email correo = new Email(email);
        String contrasenaHash = passwordHasher.hash(contrasena.valor());

        // El dominio decide si este administrador puede crear el usuario.
        Usuario nuevo = administrador.crearUsuarioConRol(id, nombre, correo, contrasenaHash, rol);

        // RN01: se consulta después de validar permisos, para no revelar qué correos existen.
        if (repository.existePorEmail(correo)) {
            throw new CorreoElectronicoDuplicadoException();
        }

        repository.guardar(nuevo);

        return nuevo;
    }
}
