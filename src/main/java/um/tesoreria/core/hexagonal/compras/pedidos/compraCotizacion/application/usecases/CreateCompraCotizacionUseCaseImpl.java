package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.exception.CompraCotizacionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in.CreateCompraCotizacionUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CreateCompraCotizacionUseCaseImpl implements CreateCompraCotizacionUseCase {

    private final CompraCotizacionRepository repository;

    @Override
    public CompraCotizacion crear(CompraCotizacion proceso) {
        if (proceso == null || proceso.getEjercicioId() == null) {
            throw new CompraCotizacionException("El ejercicio del proceso de cotización es requerido");
        }
        proceso.setEstado(CompraCotizacionEstado.BORRADOR);
        proceso.setNumero(null);
        proceso.setFechaEnvio(null);
        if (proceso.getFecha() == null) {
            proceso.setFecha(LocalDateTime.now());
        }
        return repository.save(proceso);
    }
}
