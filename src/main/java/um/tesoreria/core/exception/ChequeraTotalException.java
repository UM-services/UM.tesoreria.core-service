package um.tesoreria.core.exception;

public class ChequeraTotalException extends RuntimeException {
    public ChequeraTotalException(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId) {
        super("No se encontro el total para facultadId: " + facultadId + ", tipoChequeraId: " + tipoChequeraId + ", chequeraSerieId: " + chequeraSerieId + ", productoId: " + productoId);
    }

    public ChequeraTotalException(Long chequeraTotalId) {
        super("No se encontro el total con id: " + chequeraTotalId);
    }

}
