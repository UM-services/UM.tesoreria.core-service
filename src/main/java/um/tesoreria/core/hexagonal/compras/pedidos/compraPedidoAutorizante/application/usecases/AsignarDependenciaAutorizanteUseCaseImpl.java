package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.application.exception.CompraPedidoAutorizanteException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.domain.ports.in.AsignarDependenciaAutorizanteUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.domain.ports.out.CompraPedidoAutorizanteRepository;

@Component
@RequiredArgsConstructor
public class AsignarDependenciaAutorizanteUseCaseImpl implements AsignarDependenciaAutorizanteUseCase {

    private final CompraPedidoAutorizanteRepository compraPedidoAutorizanteRepository;

    @Override
    public void asignar(Integer autorizanteId, Integer dependenciaId) {
        if (autorizanteId == null || dependenciaId == null) {
            throw new CompraPedidoAutorizanteException("La asignación requiere autorizante y dependencia");
        }
        compraPedidoAutorizanteRepository.asignar(autorizanteId, dependenciaId);
    }

}
