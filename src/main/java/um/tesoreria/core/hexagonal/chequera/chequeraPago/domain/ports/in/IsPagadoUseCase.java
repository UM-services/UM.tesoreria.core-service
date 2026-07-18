package um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in;

public interface IsPagadoUseCase {
    boolean isPagado(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                     Integer productoId, Integer alternativaId, Integer cuotaId);
}
