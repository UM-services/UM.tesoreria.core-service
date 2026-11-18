package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.SumaCuenta;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.SumarPorCuentaUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SumarPorCuentaUseCaseImpl implements SumarPorCuentaUseCase {

    private final CuentaMovimientoRepository cuentaMovimientoRepository;

    @Override
    public List<SumaCuenta> sumarPorCuenta(OffsetDateTime desde, OffsetDateTime hasta, Byte apertura) {
        return cuentaMovimientoRepository.sumarPorCuenta(desde, hasta, apertura);
    }
}