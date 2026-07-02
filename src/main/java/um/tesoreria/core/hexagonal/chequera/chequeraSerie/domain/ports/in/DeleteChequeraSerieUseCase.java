package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in;

public interface DeleteChequeraSerieUseCase {
    void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);
}
