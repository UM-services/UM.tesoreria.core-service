package um.tesoreria.core.exception;

public class ChequeraImpresionCabeceraException extends RuntimeException {

    public ChequeraImpresionCabeceraException(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        super("No existe cabecera para facultadId=" + facultadId + ", tipoChequeraId=" + tipoChequeraId + ", chequeraSerieId=" + chequeraSerieId);
    }

}
