package com.example.ecommerce.infrastructure.rest.exception;

import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.exception.AccionSoloParaClienteException;
import com.example.ecommerce.domain.exception.CalificacionAjenaException;
import com.example.ecommerce.domain.exception.CorreoElectronicoDuplicadoException;
import com.example.ecommerce.domain.exception.CreacionUsuarioNoPermitidaException;
import com.example.ecommerce.domain.exception.CredencialesInvalidasException;
import com.example.ecommerce.domain.exception.TokenInvalidoException;
import com.example.ecommerce.domain.exception.UsuarioInactivoException;
import com.example.ecommerce.domain.exception.ReglaDominioException;
import com.example.ecommerce.domain.exception.MarcaDuplicadaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.exception.PedidoAjenoException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ArticuloNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarArticuloNoEncontrado(
            ArticuloNoEncontradoException ex) {

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

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(
            NoSuchElementException ex) {

        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                "El recurso solicitado no existe"
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
