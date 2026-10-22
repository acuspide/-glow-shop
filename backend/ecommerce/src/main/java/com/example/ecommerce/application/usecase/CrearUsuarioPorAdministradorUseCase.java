package com.example.ecommerce.application.usecase;

import com.example.ecommerce.application.PasswordHasher;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.CreacionUsuarioNoPermitidaException;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.service.VerificadorDeCorreoUnico;
import com.example.ecommerce.domain.valueobject.ContrasenaPlana;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.FechaNacimiento;
import com.example.ecommerce.domain.valueobject.RolUsuario;
import com.example.ecommerce.domain.valueobject.Telefono;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Un administrador crea a otro administrador o a un vendedor.
 * Quién puede hacerlo lo decide el dominio (Usuario.crearUsuarioConRol).
 */
@Service
@RequiredArgsConstructor
public class CrearUsuarioPorAdministradorUseCase {

    private final UsuarioRepository repository;
    private final PasswordHasher passwordHasher;
    private final VerificadorDeCorreoUnico verificadorDeCorreoUnico;

    @Transactional
    public Usuario ejecutar(
            String administradorId,
            String id,
            String nombre,
            String email,
            String contrasenaPlano,
            String telefono,
            LocalDate fechaNacimiento,
            RolUsuario rol) {

        // Si quien pide no existe, tampoco tiene permiso.
        Usuario administrador = repository.obtenerPorId(administradorId)
                .orElseThrow(CreacionUsuarioNoPermitidaException::new);

        ContrasenaPlana contrasena = new ContrasenaPlana(contrasenaPlano);
        Email correo = new Email(email);
        Telefono tel = new Telefono(telefono);
        FechaNacimiento nacimiento = new FechaNacimiento(fechaNacimiento);
        String contrasenaHash = passwordHasher.hash(contrasena.valor());

        // El dominio decide si este administrador puede crear el usuario.
        Usuario nuevo = administrador.crearUsuarioConRol(id, nombre, correo, contrasenaHash, tel, nacimiento, rol);

        // RN01: se consulta después de validar permisos, para no revelar qué correos existen.
        verificadorDeCorreoUnico.verificarQueNoEsteRegistrado(correo);

        repository.guardar(nuevo);

        return nuevo;
    }
}
