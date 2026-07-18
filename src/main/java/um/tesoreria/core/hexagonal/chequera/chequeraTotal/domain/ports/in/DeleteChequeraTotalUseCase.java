package um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.ports.in;

public interface DeleteChequeraTotalUseCase {
    void deleteAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);
}
