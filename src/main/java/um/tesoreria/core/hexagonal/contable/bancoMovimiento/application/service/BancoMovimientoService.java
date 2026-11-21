package um.tesoreria.core.hexagonal.contable.bancoMovimiento.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.model.BancoMovimiento;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.ports.in.CreateBancoMovimientoUseCase;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.ports.in.GetLastBancoMovimientoUseCase;

import java.time.OffsetDateTime;

@Service("contableBancoMovimientoService")
@RequiredArgsConstructor
public class BancoMovimientoService {

    private final CreateBancoMovimientoUseCase createBancoMovimiento;
    private final GetLastBancoMovimientoUseCase getLastBancoMovimiento;

    /**
     * Réplica de {@code clsDAOBancoMov.newId}: 1 + el último orden del banco en esa fecha.
     */
    public Long nextOrden(Long bancariaId, OffsetDateTime fecha) {
        return getLastBancoMovimiento.getLastBancoMovimiento(bancariaId, fecha)
                .map(bancoMovimiento -> bancoMovimiento.orden() + 1)
                .orElse(1L);
    }

    public BancoMovimiento create(BancoMovimiento bancoMovimiento) {
        return createBancoMovimiento.createBancoMovimiento(bancoMovimiento);
    }
}
