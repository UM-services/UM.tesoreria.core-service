package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import um.tesoreria.core.hexagonal.chequera.emision.application.exception.EmisionException;
import um.tesoreria.core.hexagonal.chequera.emision.domain.service.ReglasEmision;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.EmisionErrorResponse;

@RestControllerAdvice(basePackages = "um.tesoreria.core.hexagonal.chequera.emision")
public class EmisionExceptionHandler {

    @ExceptionHandler(EmisionException.class)
    public ResponseEntity<EmisionErrorResponse> handle(EmisionException exception) {
        return ResponseEntity.status(status(exception.getCodigo()))
                .body(new EmisionErrorResponse(exception.getCodigo(), exception.getMessage()));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<EmisionErrorResponse> handleOptimistic(ObjectOptimisticLockingFailureException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new EmisionErrorResponse(ReglasEmision.BORRADOR_DESACTUALIZADO,
                        "El borrador fue modificado por otra sesión; recargue y vuelva a intentar"));
    }

    private HttpStatus status(String codigo) {
        return switch (codigo) {
            case ReglasEmision.BORRADOR_DESACTUALIZADO, ReglasEmision.CONFLICTO_NUMERACION,
                 ReglasEmision.CLAVE_IDEMPOTENCIA_USADA, ReglasEmision.BORRADOR_NO_ABIERTO,
                 ReglasEmision.CHEQUERA_DE_BAJA -> HttpStatus.CONFLICT;
            case ReglasEmision.SIN_PERMISO_IMPRESION, ReglasEmision.SIN_PERMISO_MODIFICACION,
                 ReglasEmision.SIN_PERMISO_OVERRIDE -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.UNPROCESSABLE_ENTITY;
        };
    }
}
