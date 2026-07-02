package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in;

public interface DeleteAllByChequeraUseCase {
    void deleteAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);
}
