package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.FindAllByNumeroCuentaAndFechaContableBetweenAndAperturaUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllByNumeroCuentaAndFechaContableBetweenAndAperturaUseCaseImpl implements FindAllByNumeroCuentaAndFechaContableBetweenAndAperturaUseCase {

    private final CuentaMovimientoRepository cuentaMovimientoRepository;

    @Override
    public List<CuentaMovimiento> findAllByNumeroCuentaAndFechaContableBetweenAndApertura(
            BigDecimal numeroCuenta, OffsetDateTime desde, OffsetDateTime hasta, Byte apertura) {
        return cuentaMovimientoRepository.findAllByNumeroCuentaAndFechaContableBetweenAndApertura(
                numeroCuenta, desde, hasta, apertura);
    }
}
