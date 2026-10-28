package com.example.ecommerce.infrastructure.rest.exception;

import com.example.ecommerce.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Cualquier búsqueda por id que no encuentra nada (artículo, carrito, pedido, usuario...).
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarRecursoNoEncontrado(
            RecursoNoEncontradoException ex) {

        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(ReglaDominioException.class)
    public ResponseEntity<Map<String, Object>> manejarReglaDominio(
            ReglaDominioException ex) {

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(CorreoElectronicoDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> manejarCorreoDuplicado(
            CorreoElectronicoDuplicadoException ex) {

        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MarcaDuplicadaException.class)
    public ResponseEntity<Map<String, Object>> manejarMarcaDuplicada(
            MarcaDuplicadaException ex) {

        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }
    @ExceptionHandler(CategoriaDuplicadaException.class)
    public ResponseEntity<Map<String, Object>> manejarCategoriaDuplicada(
            CategoriaDuplicadaException ex) {

        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> manejarCredencialesInvalidas(
            CredencialesInvalidasException ex) {

        return construirRespuesta(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<Map<String, Object>> manejarTokenInvalido(
            TokenInvalidoException ex) {

        return construirRespuesta(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AccionSoloParaAdministradorException.class)
    public ResponseEntity<Map<String, Object>> manejarAccionSoloParaAdministrador(
            AccionSoloParaAdministradorException ex) {

        return construirRespuesta(HttpStatus.FORBIDDEN, ex.getMessage()); // 403
    }

    @ExceptionHandler(AccionSoloParaClienteException.class)
    public ResponseEntity<Map<String, Object>> manejarAccionSoloParaCliente(
            AccionSoloParaClienteException ex) {

        return construirRespuesta(HttpStatus.FORBIDDEN, ex.getMessage()); // 403
    }

    @ExceptionHandler(CalificacionAjenaException.class)
    public ResponseEntity<Map<String, Object>> manejarCalificacionAjena(CalificacionAjenaException ex) {
        return construirRespuesta(HttpStatus.FORBIDDEN, ex.getMessage()); // 403
    }

    @ExceptionHandler(PedidoAjenoException.class)
    public ResponseEntity<Map<String, Object>> manejarPedidoAjeno(PedidoAjenoException ex) {
        return construirRespuesta(HttpStatus.FORBIDDEN, ex.getMessage()); // 403
    }

    @ExceptionHandler(CreacionUsuarioNoPermitidaException.class)
    public ResponseEntity<Map<String, Object>> manejarCreacionNoPermitida(
            CreacionUsuarioNoPermitidaException ex) {

        return construirRespuesta(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(UsuarioInactivoException.class)
    public ResponseEntity<Map<String, Object>> manejarUsuarioInactivo(
            UsuarioInactivoException ex) {

        return construirRespuesta(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> construirRespuesta(
            HttpStatus status,
            String mensaje) {

        Map<String, Object> cuerpo = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "mensaje", mensaje
        );

        return ResponseEntity
                .status(status)
                .body(cuerpo);
    }

}
