package com.example.ecommerce.domain.exception;

public class MarcaRequeridaException extends RuntimeException {
    public MarcaRequeridaException() {
        super("La marca es obligatoria.");
    }
}
