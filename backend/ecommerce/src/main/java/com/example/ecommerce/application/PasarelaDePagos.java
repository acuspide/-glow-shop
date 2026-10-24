package com.example.ecommerce.application;

import com.example.ecommerce.domain.valueobject.MedioPago;
import com.example.ecommerce.domain.valueobject.Precio;

/**
 * Puerto para cobrar un pago. La implementación vive en infrastructure.
 */
public interface PasarelaDePagos {

    ResultadoCobro cobrar(Precio monto, MedioPago medioPago);
}