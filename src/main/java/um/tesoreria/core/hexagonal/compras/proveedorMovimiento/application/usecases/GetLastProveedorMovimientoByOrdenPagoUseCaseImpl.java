package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in.GetLastProveedorMovimientoByOrdenPagoUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;
import um.tesoreria.core.hexagonal.comprobante.application.service.ComprobanteService;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetLastProveedorMovimientoByOrdenPagoUseCaseImpl implements GetLastProveedorMovimientoByOrdenPagoUseCase {
    private static final Integer TT_PAGOS = 4;

    private final ProveedorMovimientoRepository repository;
    private final ComprobanteService comprobanteService;

    @Override
    public Optional<ProveedorMovimiento> getLastProveedorMovimientoByOrdenPago(Integer prefijo) {
        List<Integer> comprobanteIds = comprobanteService.findAllByOrdenPagoAndTipoTransaccionId(TT_PAGOS).stream()
                .map(Comprobante::getComprobanteId)
                .collect(Collectors.toList());
        return repository.findTopByPrefijoAndComprobanteIdInOrderByNumeroComprobanteDesc(prefijo, comprobanteIds);
    }
}
