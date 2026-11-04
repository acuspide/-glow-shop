package com.example.ecommerce.infrastructure.rest.exception;

import com.example.ecommerce.application.ParametroInvalidoException;
import com.example.ecommerce.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

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

    // Regla de negocio rota: el dominio rechazó la operación (por ejemplo, publicar con la tienda
    // pendiente o pasar un pedido de CONFIRMADO a ENTREGADO). La petición estaba bien formada,
    // pero el negocio no la permite: 422.
    @ExceptionHandler(ReglaDominioException.class)
    public ResponseEntity<Map<String, Object>> manejarReglaDominio(
            ReglaDominioException ex) {

        return construirRespuesta(
                HttpStatus.UNPROCESSABLE_CONTENT,
                ex.getMessage()
        );
    }

    // Datos inválidos en la petición (@Valid en el DTO): 400 con el mensaje de cada campo.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarDatosInvalidos(
            MethodArgumentNotValidException ex) {

        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining("; "));

        return construirRespuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    // Parámetro de entrada inválido (página negativa, tamaño o límite fuera de rango): 400.
    @ExceptionHandler(ParametroInvalidoException.class)
    public ResponseEntity<Map<String, Object>> manejarParametroInvalido(
            ParametroInvalidoException ex) {

        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // El cuerpo no es un JSON válido o trae un valor que no corresponde (por ejemplo, un tono que no existe): 400.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarCuerpoIlegible(
            HttpMessageNotReadableException ex) {

        return construirRespuesta(HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición no es válido: revise el formato JSON y los valores enviados.");
    }

    @ExceptionHandler(CalificacionDuplicadaException.class)
    public ResponseEntity<Map<String, Object>> manejarCalificacionDuplicada(
            CalificacionDuplicadaException ex) {

        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage()); // 409
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

    @ExceptionHandler(VendedorYaTieneTiendaException.class)
    public ResponseEntity<Map<String, Object>> manejarVendedorYaTieneTienda(VendedorYaTieneTiendaException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage()); // 409
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

    @ExceptionHandler(AccionSoloParaVendedorException.class)
    public ResponseEntity<Map<String, Object>> manejarAccionSoloParaVendedor(
            AccionSoloParaVendedorException ex) {

        return construirRespuesta(HttpStatus.FORBIDDEN, ex.getMessage()); // 403
    }

    @ExceptionHandler(TiendaAjenaException.class)
    public ResponseEntity<Map<String, Object>> manejarTiendaAjena(TiendaAjenaException ex) {
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
