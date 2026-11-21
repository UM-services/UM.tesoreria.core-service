package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import um.tesoreria.core.hexagonal.chequera.envio.application.exception.EnvioException;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto.EnvioErrorResponse;

/** Errores de negocio del envío masivo con el código como parte del contrato. */
@RestControllerAdvice(basePackages = "um.tesoreria.core.hexagonal.chequera.envio")
public class EnvioExceptionHandler {

    @ExceptionHandler(EnvioException.class)
    public ResponseEntity<EnvioErrorResponse> handle(EnvioException exception) {
        return ResponseEntity.status(status(exception.getCodigo()))
                .body(new EnvioErrorResponse(exception.getCodigo(), exception.getMessage()));
    }

    private HttpStatus status(String codigo) {
        return switch (codigo) {
            case "ENVIO_JOB_ACTIVO", "ENVIO_JOB_FINALIZADO", "ENVIO_SIN_REINTENTOS" -> HttpStatus.CONFLICT;
            case "ENVIO_JOB_INEXISTENTE" -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.UNPROCESSABLE_ENTITY;
        };
    }
}
