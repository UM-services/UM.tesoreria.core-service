package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.domain.ports.in.AsignarDependenciaAutorizanteUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.domain.ports.in.GetDependenciasAutorizadasUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.domain.ports.in.QuitarDependenciaAutorizanteUseCase;

import java.util.List;

/**
 * Fachada de las dependencias habilitadas por usuario autorizante.
 */
@Service
@RequiredArgsConstructor
public class CompraPedidoAutorizanteService {

    private final GetDependenciasAutorizadasUseCase getDependenciasAutorizadasUseCase;
    private final AsignarDependenciaAutorizanteUseCase asignarDependenciaAutorizanteUseCase;
    private final QuitarDependenciaAutorizanteUseCase quitarDependenciaAutorizanteUseCase;

    public List<Integer> getDependencias(Integer autorizanteId) {
        return getDependenciasAutorizadasUseCase.getDependencias(autorizanteId);
    }

    public void asignar(Integer autorizanteId, Integer dependenciaId) {
        asignarDependenciaAutorizanteUseCase.asignar(autorizanteId, dependenciaId);
    }

    public void quitar(Integer autorizanteId, Integer dependenciaId) {
        quitarDependenciaAutorizanteUseCase.quitar(autorizanteId, dependenciaId);
    }

}
