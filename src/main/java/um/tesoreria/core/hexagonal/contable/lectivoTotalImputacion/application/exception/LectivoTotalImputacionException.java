package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.application.exception;

import java.io.Serial;
import java.text.MessageFormat;

public class LectivoTotalImputacionException extends RuntimeException {

	@Serial
    private static final long serialVersionUID = -697565205822891525L;

	public LectivoTotalImputacionException(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId) {
		super(MessageFormat.format("Cannot find LectivoTotalImputacion {0}/{1}/{2}/{3}", facultadId, lectivoId, tipoChequeraId, productoId));
	}

	public LectivoTotalImputacionException(Long lectivoTotalImputacionId) {
		super(MessageFormat.format("Cannot find LectivoTotalImputacion {0}", lectivoTotalImputacionId));
	}

}
