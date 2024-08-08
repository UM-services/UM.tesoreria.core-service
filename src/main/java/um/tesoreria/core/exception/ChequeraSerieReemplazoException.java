package um.tesoreria.core.exception;

public class ChequeraSerieReemplazoException extends RuntimeException {

    public ChequeraSerieReemplazoException(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        super("Cannot find ChequeraSerieReemplazo with facultadId: " + facultadId + ", tipoChequeraId: " + tipoChequeraId + ", chequeraSerieId: " + chequeraSerieId);
    }

}
