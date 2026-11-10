package um.tesoreria.core.controller.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import um.tesoreria.core.service.facade.mayor.MayorConsultaException;
import um.tesoreria.core.service.facade.mayor.MayorErrorResponse;

/** Rechazos de negocio de la consulta del mayor, con cuerpo JSON {@code {status, error, codigo, message}}. */
@RestControllerAdvice(assignableTypes = BalanceController.class)
public class BalanceExceptionAdvice {

    @ExceptionHandler(MayorConsultaException.class)
    public ResponseEntity<MayorErrorResponse> mayor(MayorConsultaException ex) {
        var status = ex.getCodigo().status();
        return ResponseEntity.status(status)
                .body(new MayorErrorResponse(status.value(), status.getReasonPhrase(), ex.getCodigo().name(), ex.getMessage()));
    }
}