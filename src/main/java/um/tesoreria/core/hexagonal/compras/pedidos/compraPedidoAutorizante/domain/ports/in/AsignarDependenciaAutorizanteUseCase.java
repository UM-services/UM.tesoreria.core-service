package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.domain.ports.in;

public interface AsignarDependenciaAutorizanteUseCase {

    void asignar(Integer autorizanteId, Integer dependenciaId);

}
