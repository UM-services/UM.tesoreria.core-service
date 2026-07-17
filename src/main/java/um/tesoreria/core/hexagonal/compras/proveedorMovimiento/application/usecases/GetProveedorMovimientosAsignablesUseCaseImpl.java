package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in.GetProveedorMovimientosAsignablesUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;
import um.tesoreria.core.hexagonal.comprobante.application.service.ComprobanteService;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.kotlin.model.ProveedorArticulo;
import um.tesoreria.core.service.ProveedorArticuloService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetProveedorMovimientosAsignablesUseCaseImpl implements GetProveedorMovimientosAsignablesUseCase {
    private final ProveedorMovimientoRepository repository;
    private final ComprobanteService comprobanteService;
    private final ProveedorArticuloService proveedorArticuloService;

    @Override
    public List<ProveedorMovimiento> getProveedorMovimientosAsignables(
            Integer proveedorId, OffsetDateTime desde, OffsetDateTime hasta,
            Integer geograficaId, Boolean todos) {

        List<Comprobante> comprobantes = comprobanteService.findAllByTipoTransaccionId(3);
        List<Integer> comprobanteIds = comprobantes.stream()
                .map(Comprobante::getComprobanteId)
                .collect(Collectors.toList());

        List<Long> proveedorMovimientoIds;
        if (geograficaId == 0) {
            List<ProveedorMovimiento> proveedorMovimientos = repository
                    .findAllByProveedorIdAndFechaComprobanteBetweenAndComprobanteIdInAndFechaAnulacionIsNull(
                            proveedorId, desde, hasta, comprobanteIds);
            proveedorMovimientoIds = proveedorMovimientos.stream()
                    .map(ProveedorMovimiento::getProveedorMovimientoId)
                    .collect(Collectors.toList());
        } else {
            List<ProveedorMovimiento> proveedorMovimientos = repository
                    .findAllByProveedorIdAndFechaComprobanteBetweenAndComprobanteIdInAndGeograficaIdAndFechaAnulacionIsNull(
                            proveedorId, desde, hasta, comprobanteIds, geograficaId);
            proveedorMovimientoIds = proveedorMovimientos.stream()
                    .map(ProveedorMovimiento::getProveedorMovimientoId)
                    .collect(Collectors.toList());
        }

        List<ProveedorArticulo> proveedorArticulos = proveedorArticuloService
                .findAllByProveedorMovimientoIds(proveedorMovimientoIds, true);

        if (todos) {
            proveedorArticulos = proveedorArticulos.stream()
                    .filter(p -> p.getAsignado().compareTo(p.getPrecioFinal()) != 0)
                    .toList();
        }

        proveedorMovimientoIds = proveedorArticulos.stream()
                .map(ProveedorArticulo::getProveedorMovimientoId)
                .collect(Collectors.toList());

        return repository.findAllByIdsSortedByFechaComprobanteDesc(proveedorMovimientoIds);
    }
}
