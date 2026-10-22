package com.example.ecommerce.application.usecase;

import com.example.ecommerce.application.PasswordHasher;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.service.VerificadorDeCorreoUnico;
import com.example.ecommerce.domain.valueobject.ContrasenaPlana;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.FechaNacimiento;
import com.example.ecommerce.domain.valueobject.Telefono;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Autorregistro de vendedor: una persona crea su propia cuenta de vendedor.
 * Siempre queda como VENDEDOR; para publicar necesitará que un administrador apruebe su tienda (RN05).
 */
@Service
@RequiredArgsConstructor
public class RegistrarVendedorUseCase {

    private final UsuarioRepository repository;
    private final PasswordHasher passwordHasher;
    private final VerificadorDeCorreoUnico verificadorDeCorreoUnico;

    @Transactional
    public Usuario ejecutar(String id, String nombre, String email, String contrasenaPlano,
                            String telefono, LocalDate fechaNacimiento) {
        ContrasenaPlana contrasena = new ContrasenaPlana(contrasenaPlano); // el dominio valida
        Email correo = new Email(email);                                   // el dominio valida
        Telefono tel = new Telefono(telefono);                             // el dominio valida
        FechaNacimiento nacimiento = new FechaNacimiento(fechaNacimiento); // el dominio valida

        // RN01: el correo debe ser único.
        verificadorDeCorreoUnico.verificarQueNoEsteRegistrado(correo);

        String contrasenaHash = passwordHasher.hash(contrasena.valor());
        Usuario vendedor = Usuario.registrarVendedor(id, nombre, correo, contrasenaHash, tel, nacimiento);

        repository.guardar(vendedor);

        return vendedor;
    }
}
