package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.application.exception.CompraPedidoAutorizanteException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.domain.ports.in.QuitarDependenciaAutorizanteUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.domain.ports.out.CompraPedidoAutorizanteRepository;

@Component
@RequiredArgsConstructor
public class QuitarDependenciaAutorizanteUseCaseImpl implements QuitarDependenciaAutorizanteUseCase {

    private final CompraPedidoAutorizanteRepository compraPedidoAutorizanteRepository;

    @Override
    public void quitar(Integer autorizanteId, Integer dependenciaId) {
        if (autorizanteId == null || dependenciaId == null) {
            throw new CompraPedidoAutorizanteException("La baja requiere autorizante y dependencia");
        }
        compraPedidoAutorizanteRepository.quitar(autorizanteId, dependenciaId);
    }

}
