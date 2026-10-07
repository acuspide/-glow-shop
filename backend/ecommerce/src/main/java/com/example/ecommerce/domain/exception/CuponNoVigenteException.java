package com.example.ecommerce.domain.exception;

public class CuponNoVigenteException extends ReglaDominioException{
    public CuponNoVigenteException() {
        super("El cupón no está vigente.");
    }
}
