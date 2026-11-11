package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception;

import java.io.Serial;

/**
 * Se lanza cuando una cuota a emitir apunta a un par producto/alternativa que no quedó copiado en
 * {@code chequera_alternativa}. Sin esta fila el INSERT de {@code chequera_cuota} viola la foreign
 * key {@code chequera_cuota_ibfk_3} y la transacción muere con un 500 opaco de MySQL; cortar acá
 * deja el origen del problema escrito en el log.
 */
public class ChequeraAlternativaFaltanteException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 4657299104412755331L;

	public ChequeraAlternativaFaltanteException(Integer facultadId, Integer lectivoId, Integer tipoChequeraId,
			Long chequeraSerieId, Integer productoId, Integer alternativaId) {
		super("Cannot create ChequeraCuota: missing ChequeraAlternativa " + facultadId + "/" + tipoChequeraId + "/"
				+ chequeraSerieId + "/" + productoId + "/" + alternativaId
				+ ". Revisar lectivo_alternativa para facultadId=" + facultadId + " lectivoId=" + lectivoId
				+ " tipoChequeraId=" + tipoChequeraId + " productoId=" + productoId
				+ " alternativaId=" + alternativaId);
	}

}
