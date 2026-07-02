package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in;

import java.math.BigDecimal;

public interface CalculateTotalCuotasPagadasUseCase {
    BigDecimal calculateTotalCuotasPagadas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId);
}
