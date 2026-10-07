package com.example.ecommerce.infrastructure;

import com.example.ecommerce.application.usecase.CrearAdministradorInicialUseCase;
import com.example.ecommerce.infrastructure.persistence.SecuenciaIdUsuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Al arrancar, deja listo el primer administrador (datos en application.properties).
 */
@Component
public class AdministradorInicialInicializador implements CommandLineRunner {

    private final CrearAdministradorInicialUseCase useCase;
    private final SecuenciaIdUsuario secuencia;
    private final String nombre;
    private final String email;
    private final String contrasena;
    private final String telefono;
    private final String fechaNacimiento;

    public AdministradorInicialInicializador(
            CrearAdministradorInicialUseCase useCase,
            SecuenciaIdUsuario secuencia,
            @Value("${glow.admin.nombre}") String nombre,
            @Value("${glow.admin.email}") String email,
            @Value("${glow.admin.password}") String contrasena,
            @Value("${glow.admin.telefono}") String telefono,
            @Value("${glow.admin.fecha-nacimiento}") String fechaNacimiento) {
        this.useCase = useCase;
        this.secuencia = secuencia;
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
    }

    @Override
    public void run(String... args) {
        useCase.ejecutar(secuencia.siguiente(), nombre, email, contrasena,
                telefono, LocalDate.parse(fechaNacimiento));
    }
}
