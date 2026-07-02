package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in;

import java.math.BigDecimal;

public interface CalculateTotalCuotasActivasUseCase {
    BigDecimal calculateTotalCuotasActivas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId);
}
