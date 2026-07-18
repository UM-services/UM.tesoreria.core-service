package um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in;

public interface GetNextOrdenUseCase {
    Integer getNextOrden(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                         Integer productoId, Integer alternativaId, Integer cuotaId);
}
