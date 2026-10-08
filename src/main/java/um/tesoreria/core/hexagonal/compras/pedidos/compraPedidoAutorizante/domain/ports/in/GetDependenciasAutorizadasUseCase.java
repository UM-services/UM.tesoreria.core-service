package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.domain.ports.in;

import java.util.List;

public interface GetDependenciasAutorizadasUseCase {

    List<Integer> getDependencias(Integer autorizanteId);

}
