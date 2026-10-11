package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.exception.CompraCotizacionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedor;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in.MarcarNoCotizaCompraCotizacionUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionRepository;

@Component
@RequiredArgsConstructor
public class MarcarNoCotizaCompraCotizacionUseCaseImpl implements MarcarNoCotizaCompraCotizacionUseCase {

    private final CompraCotizacionRepository repository;

    @Override
    public CompraCotizacionProveedor marcarNoCotiza(Long compraCotizacionId, Long compraCotizacionProveedorId,
                                                    String observacion) {
        CompraCotizacionProveedor proveedor = repository.findProveedorById(compraCotizacionProveedorId)
                .orElseThrow(() -> new CompraCotizacionException(
                        "No existe el documento de proveedor " + compraCotizacionProveedorId));
        if (!compraCotizacionId.equals(proveedor.getCompraCotizacionId())) {
            throw new IllegalStateException("El documento de proveedor no pertenece al proceso " + compraCotizacionId);
        }
        proveedor.marcarNoCotiza(observacion);
        return repository.saveProveedor(proveedor);
    }
}
