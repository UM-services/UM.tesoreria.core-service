package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in.GetProveedorMovimientoByOrdenPagoUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;
import um.tesoreria.core.hexagonal.comprobante.application.service.ComprobanteService;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetProveedorMovimientoByOrdenPagoUseCaseImpl implements GetProveedorMovimientoByOrdenPagoUseCase {
    private final ProveedorMovimientoRepository repository;
    private final ComprobanteService comprobanteService;

    @Override
    public Optional<ProveedorMovimiento> getProveedorMovimientoByOrdenPago(Integer prefijo, Long numeroComprobante) {
        List<Integer> comprobanteIds = comprobanteService.findAllByOrdenPago().stream()
                .map(Comprobante::getComprobanteId)
                .collect(Collectors.toList());
        return repository.findByPrefijoAndNumeroComprobanteAndComprobanteIdIn(prefijo, numeroComprobante, comprobanteIds);
    }
}
