package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.exception.AsientoConflictoException;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.DeleteAsientoUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DeleteAsientoUseCaseImpl implements DeleteAsientoUseCase {

    private final CuentaMovimientoRepository cuentaMovimientoRepository;

    @Override
    public void deleteAsiento(OffsetDateTime fechaContable, Integer ordenContable) {
        cuentaMovimientoRepository.deleteAllByFechaContableAndOrdenContable(fechaContable, ordenContable);
    }

    @Override
    public void deleteAsiento(OffsetDateTime fechaContable, Integer ordenContable, Integer comprobanteId) {
        List<CuentaMovimiento> lineas = cuentaMovimientoRepository
                .findAllByFechaContableAndOrdenContableAndItemGreaterThanEqual(fechaContable, ordenContable, 0);
        boolean hayAjenas = lineas.stream()
                .anyMatch(linea -> !Objects.equals(linea.getComprobanteId(), comprobanteId));
        if (hayAjenas) {
            throw new AsientoConflictoException(
                    "El asiento " + fechaContable.toLocalDate() + "/" + ordenContable
                            + " tiene líneas de otro comprobante; no se borra nada");
        }
        List<Long> ids = lineas.stream()
                .map(CuentaMovimiento::getCuentaMovimientoId)
                .toList();
        if (!ids.isEmpty()) {
            cuentaMovimientoRepository.deleteAllByCuentaMovimientoIdIn(ids);
        }
    }

}
