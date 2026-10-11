package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.exception.CompraCotizacionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in.UpdateCompraCotizacionUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionRepository;

@Component
@RequiredArgsConstructor
public class UpdateCompraCotizacionUseCaseImpl implements UpdateCompraCotizacionUseCase {

    private final CompraCotizacionRepository repository;

    @Override
    public CompraCotizacion actualizar(Long compraCotizacionId, CompraCotizacion datos) {
        CompraCotizacion proceso = repository.findById(compraCotizacionId)
                .orElseThrow(() -> new CompraCotizacionException(compraCotizacionId));
        if (!proceso.esEditable()) {
            throw new IllegalStateException("Sólo un borrador puede editarse (estado actual: " + proceso.getEstado() + ")");
        }
        if (datos != null) {
            proceso.setObservacion(datos.getObservacion());
            proceso.setFechaLimiteRespuesta(datos.getFechaLimiteRespuesta());
            proceso.setMontoEstimado(datos.getMontoEstimado());
            proceso.setCantidadMinima(datos.getCantidadMinima());
            proceso.setExcepcionMotivo(datos.getExcepcionMotivo());
            proceso.setUsuarioId(datos.getUsuarioId());
            if (datos.getItems() != null) {
                proceso.setItems(datos.getItems());
            }
            if (datos.getProveedores() != null) {
                proceso.setProveedores(datos.getProveedores());
            }
        }
        return repository.save(proceso);
    }
}
