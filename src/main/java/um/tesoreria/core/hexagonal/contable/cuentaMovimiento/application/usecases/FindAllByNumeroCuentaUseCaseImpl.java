package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.FindAllByNumeroCuentaUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllByNumeroCuentaUseCaseImpl implements FindAllByNumeroCuentaUseCase {

    private final CuentaMovimientoRepository cuentaMovimientoRepository;

    @Override
    public List<CuentaMovimiento> findAllByNumeroCuenta(BigDecimal numeroCuenta, Boolean onlyOne) {
        if (onlyOne) {
            return cuentaMovimientoRepository.findTopByNumeroCuenta(numeroCuenta)
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList());
        }
        return cuentaMovimientoRepository.findAllByNumeroCuenta(numeroCuenta);
    }
}
