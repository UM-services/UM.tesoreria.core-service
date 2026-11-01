package um.tesoreria.core.controller;

import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import um.tesoreria.core.controller.facade.AsientoInflacionController;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioNoEncontradoException;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioSolicitudInvalidaException;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.web.dto.EjercicioErrorResponse;

/**
 * Errores del slice contable/ejercicio para los dos controladores legacy que lo usan: la fachada de escrituras y
 * la generación de asientos de inflación (que actualiza la orden contable por el mismo caso de uso). VB6 necesita
 * un cuerpo JSON en cada rechazo; sin este advice el controlador de inflación respondería un 500 sin cuerpo.
 * Los GET de EjercicioController también pasan por acá, pero no lanzan estas excepciones.
 */
@RestControllerAdvice(assignableTypes = {EjercicioController.class, AsientoInflacionController.class})
public class EjercicioExceptionAdvice {

	@ExceptionHandler(EjercicioConflictoException.class)
	public ResponseEntity<EjercicioErrorResponse> conflicto(EjercicioConflictoException ex) {
		return error(HttpStatus.CONFLICT, ex.getCodigo().name(), ex.getMessage());
	}

	@ExceptionHandler(PessimisticLockingFailureException.class)
	public ResponseEntity<EjercicioErrorResponse> concurrencia(PessimisticLockingFailureException ex) {
		return error(HttpStatus.CONFLICT, EjercicioConflictoException.Codigo.CONCURRENCIA.name(),
				"El ejercicio está siendo modificado por otra operación; reintentar");
	}

	@ExceptionHandler(EjercicioNoEncontradoException.class)
	public ResponseEntity<EjercicioErrorResponse> noEncontrado(EjercicioNoEncontradoException ex) {
		return error(HttpStatus.NOT_FOUND, "NO_ENCONTRADO", ex.getMessage());
	}

	@ExceptionHandler({EjercicioSolicitudInvalidaException.class, HttpMessageNotReadableException.class})
	public ResponseEntity<EjercicioErrorResponse> solicitudInvalida(Exception ex) {
		return error(HttpStatus.BAD_REQUEST, "SOLICITUD_INVALIDA",
				ex instanceof EjercicioSolicitudInvalidaException ? ex.getMessage() : "Cuerpo de la solicitud mal formado");
	}

	private static ResponseEntity<EjercicioErrorResponse> error(HttpStatus status, String codigo, String mensaje) {
		return ResponseEntity.status(status)
				.body(new EjercicioErrorResponse(status.value(), status.getReasonPhrase(), codigo, mensaje));
	}
}
