package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.exception.AsientoIngresoException;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.controller.AsientoIngresoController;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto.AsientoIngresoErrorResponse;

/** Rechazos del slice con cuerpo JSON {@code {status, error, codigo, message}}. */
@RestControllerAdvice(assignableTypes = AsientoIngresoController.class)
public class AsientoIngresoExceptionAdvice {

    @ExceptionHandler(AsientoIngresoException.class)
    public ResponseEntity<AsientoIngresoErrorResponse> negocio(AsientoIngresoException ex) {
        var status = ex.getCodigo().status();
        return ResponseEntity.status(status)
                .body(new AsientoIngresoErrorResponse(status.value(), status.getReasonPhrase(),
                        ex.getCodigo().name(), ex.getMessage()));
    }

    @ExceptionHandler({PessimisticLockingFailureException.class, DataIntegrityViolationException.class})
    public ResponseEntity<AsientoIngresoErrorResponse> concurrencia(Exception ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new AsientoIngresoErrorResponse(HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase(), "CONCURRENCIA",
                        "El asiento está siendo modificado por otra operación; reintentar"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AsientoIngresoErrorResponse> solicitudInvalida(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new AsientoIngresoErrorResponse(HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(), "SOLICITUD_INVALIDA", ex.getMessage()));
    }

}
