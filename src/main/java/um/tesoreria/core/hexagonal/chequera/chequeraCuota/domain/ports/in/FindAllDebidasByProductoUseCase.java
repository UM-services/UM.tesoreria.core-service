package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;

import java.time.OffsetDateTime;
import java.util.List;

public interface FindAllDebidasByProductoUseCase {
    List<ChequeraCuota> findAllDebidasByProducto(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Integer productoId, OffsetDateTime referencia);
}
