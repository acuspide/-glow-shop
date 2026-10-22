package com.example.ecommerce.domain.exception;

public class TransaccionRequeridaException extends RuntimeException {
  public TransaccionRequeridaException(String message) {
    super(message);
  }
}
