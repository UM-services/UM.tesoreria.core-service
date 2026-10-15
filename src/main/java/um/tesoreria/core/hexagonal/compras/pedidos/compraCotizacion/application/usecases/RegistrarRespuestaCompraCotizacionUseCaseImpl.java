package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.exception.CompraCotizacionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedor;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in.RegistrarRespuestaCompraCotizacionUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionRepository;

@Component
@RequiredArgsConstructor
public class RegistrarRespuestaCompraCotizacionUseCaseImpl implements RegistrarRespuestaCompraCotizacionUseCase {

    private final CompraCotizacionRepository repository;

    @Override
    public CompraCotizacionProveedor registrarRespuesta(Long compraCotizacionId, Long compraCotizacionProveedorId,
                                                        CompraCotizacionProveedor respuesta) {
        CompraCotizacion proceso = repository.findById(compraCotizacionId)
                .orElseThrow(() -> new CompraCotizacionException(compraCotizacionId));
        if (!proceso.estaEnviada()) {
            throw new IllegalStateException("El proceso no está enviado (estado actual: " + proceso.getEstado() + ")");
        }
        CompraCotizacionProveedor proveedor = repository.findProveedorById(compraCotizacionProveedorId)
                .orElseThrow(() -> new CompraCotizacionException(
                        "No existe el documento de proveedor " + compraCotizacionProveedorId));
        if (!compraCotizacionId.equals(proveedor.getCompraCotizacionId())) {
            throw new IllegalStateException("El documento de proveedor no pertenece al proceso " + compraCotizacionId);
        }
        proveedor.registrarRespuesta(respuesta.getFechaRespuesta(), respuesta.getMoneda(), respuesta.getTotal(),
                respuesta.getPlazoEntrega(), respuesta.getCondicionPago(), respuesta.getGarantia(),
                respuesta.getValidezOferta());
        proveedor.setItems(respuesta.getItems());
        return repository.saveProveedor(proveedor);
    }
}
