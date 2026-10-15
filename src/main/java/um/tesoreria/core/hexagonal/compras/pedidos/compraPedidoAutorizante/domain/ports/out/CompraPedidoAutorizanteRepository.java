package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.domain.ports.out;

import java.util.List;

public interface CompraPedidoAutorizanteRepository {

    List<Integer> findDependenciaIdsByAutorizanteId(Integer autorizanteId);

    void asignar(Integer autorizanteId, Integer dependenciaId);

    void quitar(Integer autorizanteId, Integer dependenciaId);

    boolean existe(Integer autorizanteId, Integer dependenciaId);

}
