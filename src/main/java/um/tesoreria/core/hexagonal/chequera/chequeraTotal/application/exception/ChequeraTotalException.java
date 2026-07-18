package um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.exception;

import java.text.MessageFormat;

public class ChequeraTotalException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ChequeraTotalException(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId) {
        super(MessageFormat.format("Cannot find ChequeraTotal for facultadId: {0}, tipoChequeraId: {1}, chequeraSerieId: {2}, productoId: {3}",
                facultadId, tipoChequeraId, chequeraSerieId, productoId));
    }

    public ChequeraTotalException(Long chequeraTotalId) {
        super(MessageFormat.format("Cannot find ChequeraTotal {0}", chequeraTotalId));
    }

    public ChequeraTotalException() {
        super("Cannot find ChequeraTotal");
    }
}
