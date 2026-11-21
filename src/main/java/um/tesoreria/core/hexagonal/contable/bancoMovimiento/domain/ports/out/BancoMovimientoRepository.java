package um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.ports.out;

import um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.model.BancoMovimiento;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface BancoMovimientoRepository {

    Optional<BancoMovimiento> findByBancariaIdAndFechaOrderByOrdenDesc(Long bancariaId, OffsetDateTime fecha);

    BancoMovimiento save(BancoMovimiento bancoMovimiento);
}
