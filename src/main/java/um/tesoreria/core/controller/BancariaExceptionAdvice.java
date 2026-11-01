package um.tesoreria.core.controller;

import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaConflictoException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaNoEncontradaException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaSolicitudInvalidaException;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.web.dto.BancariaErrorResponse;

/**
 * Errores del slice contable/bancaria para su fachada legacy. VB6 reconoce un rechazo por la clave status del
 * cuerpo JSON: cada error lleva cuerpo. El GET / del listado legacy también pasa por acá y no lanza estas excepciones.
 */
@RestControllerAdvice(assignableTypes = BancariaController.class)
public class BancariaExceptionAdvice {

	@ExceptionHandler(BancariaSolicitudInvalidaException.class)
	public ResponseEntity<BancariaErrorResponse> solicitudInvalida(BancariaSolicitudInvalidaException ex) {
		return error(HttpStatus.BAD_REQUEST, ex.getCodigo().name(), ex.getMessage());
	}

	@ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
	public ResponseEntity<BancariaErrorResponse> cuerpoOParametroInvalido(Exception ex) {
		return error(HttpStatus.BAD_REQUEST, BancariaSolicitudInvalidaException.Codigo.SOLICITUD_INVALIDA.name(),
				ex instanceof HttpMessageNotReadableException ? "Cuerpo de la solicitud mal formado" : "Parámetro de la ruta inválido");
	}

	@ExceptionHandler(BancariaNoEncontradaException.class)
	public ResponseEntity<BancariaErrorResponse> noEncontrada(BancariaNoEncontradaException ex) {
		return error(HttpStatus.NOT_FOUND, "NO_ENCONTRADA", ex.getMessage());
	}

	@ExceptionHandler(BancariaConflictoException.class)
	public ResponseEntity<BancariaErrorResponse> conflicto(BancariaConflictoException ex) {
		return error(HttpStatus.CONFLICT, ex.getCodigo().name(), ex.getMessage());
	}

	/** Lock wait timeout o deadlock sobre la fila: otra operación la tiene tomada. */
	@ExceptionHandler(PessimisticLockingFailureException.class)
	public ResponseEntity<BancariaErrorResponse> concurrencia(PessimisticLockingFailureException ex) {
		return error(HttpStatus.CONFLICT, BancariaConflictoException.Codigo.CONFLICTO.name(),
				"La cuenta bancaria está siendo modificada por otra operación; reintentar");
	}

	private static ResponseEntity<BancariaErrorResponse> error(HttpStatus status, String codigo, String mensaje) {
		return ResponseEntity.status(status)
				.body(new BancariaErrorResponse(status.value(), status.getReasonPhrase(), codigo, mensaje));
	}
}
