package com.example.ecommerce;

import com.example.ecommerce.domain.valueobject.FechaNacimiento;
import com.example.ecommerce.domain.valueobject.Telefono;

import java.time.LocalDate;

/** Datos válidos reutilizables en las pruebas de usuario. */
public final class DatosPrueba {

    public static final String TELEFONO_TEXTO = "3001234567";
    public static final LocalDate FECHA_VALOR = LocalDate.of(2000, 1, 1);
    public static final Telefono TELEFONO = new Telefono(TELEFONO_TEXTO);
    public static final FechaNacimiento FECHA = new FechaNacimiento(FECHA_VALOR);

    private DatosPrueba() {
    }
}
