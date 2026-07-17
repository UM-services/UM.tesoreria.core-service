package um.tesoreria.core.hexagonal.compras.facturaPendiente.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.facturaPendiente.domain.model.FacturaPendiente;
import um.tesoreria.core.hexagonal.compras.facturaPendiente.domain.ports.out.FacturaPendienteRepository;
import um.tesoreria.core.hexagonal.compras.facturaPendiente.infrastructure.persistence.repository.JpaFacturaPendienteRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaFacturaPendienteRepositoryAdapter implements FacturaPendienteRepository {
    private final JpaFacturaPendienteRepository repository;

    @Override
    public void updateFechaPagoInProveedorPago() {
        repository.updateFechaPagoInProveedorPago();
    }

    @Override
    public List<FacturaPendiente> findFacturasPendientes(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta) {
        return repository.findFacturasPendientes(fechaDesde, fechaHasta).stream()
                .map(entity -> FacturaPendiente.builder()
                        .proveedorMovimientoId(entity.getProveedorMovimientoId())
                        .razonSocial(entity.getRazonSocial())
                        .cuit(entity.getCuit())
                        .cbu(entity.getCbu())
                        .fechaComprobante(entity.getFechaComprobante())
                        .fechaVencimiento(entity.getFechaVencimiento())
                        .observaciones(entity.getObservaciones())
                        .comprobante(entity.getComprobante())
                        .debita(entity.getDebita())
                        .prefijo(entity.getPrefijo())
                        .numeroComprobante(entity.getNumeroComprobante())
                        .importeFactura(entity.getImporteFactura())
                        .importePagado(entity.getImportePagado())
                        .build())
                .collect(Collectors.toList());
    }
}
