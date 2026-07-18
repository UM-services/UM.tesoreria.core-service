package um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in;

public interface DeletePagosByChequeraUseCase {
    void deletePagosByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);
}
