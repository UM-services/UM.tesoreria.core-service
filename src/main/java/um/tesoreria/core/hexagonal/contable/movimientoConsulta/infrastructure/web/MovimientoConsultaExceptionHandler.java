package um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.exception.MovimientoConsultaException;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.dto.MovimientoConsultaErrorResponse;

@RestControllerAdvice(basePackages = "um.tesoreria.core.hexagonal.contable.movimientoConsulta")
public class MovimientoConsultaExceptionHandler {

    @ExceptionHandler(MovimientoConsultaException.class)
    public ResponseEntity<MovimientoConsultaErrorResponse> handle(MovimientoConsultaException exception) {
        HttpStatus status = switch (exception.getCodigo()) {
            case FECHAS_REQUERIDAS, FECHA_FUERA_DE_RANGO, CUERPO_INVALIDO -> HttpStatus.BAD_REQUEST;
            case DESDE_POSTERIOR, RANGO_DEMASIADO_LARGO, DEMASIADOS_MOVIMIENTOS -> HttpStatus.CONFLICT;
        };
        return respuesta(status, exception.getCodigo(), exception.getMessage());
    }

    /** Cuerpo que Jackson no puede leer: JSON roto o una fecha sin hora ni offset. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MovimientoConsultaErrorResponse> handleCuerpo(HttpMessageNotReadableException exception) {
        return respuesta(HttpStatus.BAD_REQUEST, MovimientoConsultaException.Codigo.CUERPO_INVALIDO,
                "El cuerpo no es válido: desde y hasta son fechas con hora y offset, por ejemplo 2025-03-01T00:00:00-03:00");
    }

    private static ResponseEntity<MovimientoConsultaErrorResponse> respuesta(HttpStatus status,
                                                                             MovimientoConsultaException.Codigo codigo,
                                                                             String message) {
        return ResponseEntity.status(status).body(new MovimientoConsultaErrorResponse(status.value(),
                status.getReasonPhrase(), codigo.name(), message));
    }
}
