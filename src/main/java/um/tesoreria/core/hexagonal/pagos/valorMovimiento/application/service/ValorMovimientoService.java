package um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.exception.ValorMovimientoException;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.ValorMovimiento;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.in.CreateValorMovimientoUseCase;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.in.GetValorMovimientoByIdUseCase;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.in.GetValorMovimientosByIdInUseCase;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.in.UpdateValorMovimientoAsientoUseCase;

import java.time.OffsetDateTime;
import java.util.List;

@Service("pagosValorMovimientoService")
@RequiredArgsConstructor
public class ValorMovimientoService {

    private final CreateValorMovimientoUseCase createValorMovimientoUseCase;
    private final GetValorMovimientoByIdUseCase getValorMovimientoByIdUseCase;
    private final GetValorMovimientosByIdInUseCase getValorMovimientosByIdInUseCase;
    private final UpdateValorMovimientoAsientoUseCase updateValorMovimientoAsientoUseCase;

    public ValorMovimiento create(ValorMovimiento valorMovimiento) {
        return createValorMovimientoUseCase.createValorMovimiento(valorMovimiento);
    }

    public ValorMovimiento findById(Long valorMovimientoId) {
        return getValorMovimientoByIdUseCase.getValorMovimientoById(valorMovimientoId)
                .orElseThrow(() -> new ValorMovimientoException(valorMovimientoId));
    }

    public List<ValorMovimiento> findAllByIdIn(List<Long> valorMovimientoIds) {
        return getValorMovimientosByIdInUseCase.getValorMovimientos(valorMovimientoIds);
    }

    public ValorMovimiento updateAsiento(Long valorMovimientoId, OffsetDateTime fechaContable, Integer ordenContable) {
        return updateValorMovimientoAsientoUseCase.updateAsiento(valorMovimientoId, fechaContable, ordenContable);
    }
}
