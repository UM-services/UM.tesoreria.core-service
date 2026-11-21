package um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.model.BancoMovimiento;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface GetLastBancoMovimientoUseCase {

    Optional<BancoMovimiento> getLastBancoMovimiento(Long bancariaId, OffsetDateTime fecha);
}
