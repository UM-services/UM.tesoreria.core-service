package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.FindAllByAsientoUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllByAsientoUseCaseImpl implements FindAllByAsientoUseCase {

    private final CuentaMovimientoRepository cuentaMovimientoRepository;

    @Override
    public List<CuentaMovimiento> findAllByAsiento(OffsetDateTime fechaContable, Integer ordenContable, Integer itemDesde, Integer debita) {
        if (debita < 2) {
            return cuentaMovimientoRepository.findAllByFechaContableAndOrdenContableAndDebitaAndItemGreaterThanEqual(
                    fechaContable, ordenContable, debita.byteValue(), itemDesde);
        }
        return cuentaMovimientoRepository.findAllByFechaContableAndOrdenContableAndItemGreaterThanEqual(
                fechaContable, ordenContable, itemDesde);
    }
}
