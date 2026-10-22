package com.example.ecommerce.domain.exception;

public class MedioPagoRequeridoException extends RuntimeException {
  public MedioPagoRequeridoException(String message) {
    super(message);
  }
}
