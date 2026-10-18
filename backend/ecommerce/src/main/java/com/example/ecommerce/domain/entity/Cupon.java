package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.CodigoCuponRequeridoException;
import com.example.ecommerce.domain.exception.FechaVencimientoRequeridaException;
import com.example.ecommerce.domain.exception.PorcentajeDescuentoInvalidoException;
import com.example.ecommerce.domain.valueobject.Precio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Cupon {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    private final long id;
    private final String codigo;
    private final int porcentaje;
    private final LocalDate fechaExpiracion;

    private Cupon(long id, String codigo, int porcentaje, LocalDate fechaExpiracion) {
        this.id = id;
        this.codigo = codigo;
        this.porcentaje = porcentaje;
        this.fechaExpiracion = fechaExpiracion;
    }

    public static Cupon crear(long id, String codigo, int porcentaje, LocalDate fechaExpiracion) {
        validarCodigo(codigo);
        validarPorcentaje(porcentaje);
        validarFechaExpiracion(fechaExpiracion);
        return new Cupon(id, codigo, porcentaje, fechaExpiracion);
    }

    private static void validarCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new CodigoCuponRequeridoException();
        }
    }

    private static void validarPorcentaje(int porcentaje) {
        if (porcentaje <= 0 || porcentaje > 100) {
            throw new PorcentajeDescuentoInvalidoException();
        }
    }

    private static void validarFechaExpiracion(LocalDate fechaExpiracion) {
        if (fechaExpiracion == null) {
            throw new FechaVencimientoRequeridaException();
        }
    }

    public boolean estaVigente() {
        return !LocalDate.now().isAfter(fechaExpiracion);
    }

    public Precio calcularDescuento(BigDecimal subtotal) {
        BigDecimal descuento = subtotal
                .multiply(BigDecimal.valueOf(porcentaje))
                .divide(CIEN);
        return new Precio(descuento);
    }

    public long getId() { return id; }
    public String getCodigo() { return codigo; }
    public int getPorcentaje() { return porcentaje; }
    public LocalDate getFechaExpiracion() { return fechaExpiracion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cupon otro)) return false;
        return id == otro.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
