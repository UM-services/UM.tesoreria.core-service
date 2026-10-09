package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.domain.ports.in;

public interface QuitarDependenciaAutorizanteUseCase {

    void quitar(Integer autorizanteId, Integer dependenciaId);

}
