package um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.repository;

import java.math.BigDecimal;
import org.springframework.data.repository.Repository;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.entity.CuentaMovimientoReferenciaEntity;

public interface CuentaMovimientoReferenciaRepository extends Repository<CuentaMovimientoReferenciaEntity, Long> {
    boolean existsByNumeroCuentaBetween(BigDecimal desde, BigDecimal hasta);
}
