package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.exception.CompraCotizacionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in.FormalizarCompraCotizacionUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionRepository;

@Component
@RequiredArgsConstructor
public class FormalizarCompraCotizacionUseCaseImpl implements FormalizarCompraCotizacionUseCase {

    private final CompraCotizacionRepository compraCotizacionRepository;

    @Override
    public CompraCotizacion formalizar(Long compraCotizacionId) {
        CompraCotizacion proceso = compraCotizacionRepository.findById(compraCotizacionId)
                .orElseThrow(() -> new CompraCotizacionException(compraCotizacionId));
        proceso.formalizar();
        return compraCotizacionRepository.updateHeader(proceso);
    }

}
