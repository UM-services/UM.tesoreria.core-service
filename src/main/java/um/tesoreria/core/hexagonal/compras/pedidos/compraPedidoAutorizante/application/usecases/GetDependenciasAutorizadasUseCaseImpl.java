package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.application.exception.CompraPedidoAutorizanteException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.domain.ports.in.GetDependenciasAutorizadasUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.domain.ports.out.CompraPedidoAutorizanteRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetDependenciasAutorizadasUseCaseImpl implements GetDependenciasAutorizadasUseCase {

    private final CompraPedidoAutorizanteRepository compraPedidoAutorizanteRepository;

    @Override
    public List<Integer> getDependencias(Integer autorizanteId) {
        if (autorizanteId == null) {
            throw new CompraPedidoAutorizanteException("Falta el autorizante");
        }
        return compraPedidoAutorizanteRepository.findDependenciaIdsByAutorizanteId(autorizanteId);
    }

}
