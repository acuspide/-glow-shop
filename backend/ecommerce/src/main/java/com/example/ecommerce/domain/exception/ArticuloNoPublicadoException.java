package com.example.ecommerce.domain.exception;

public class ArticuloNoPublicadoException extends ReglaDominioException {

  public ArticuloNoPublicadoException() {
    super("No se puede agregar a la rutina un artículo que no esté publicado");
  }
}
