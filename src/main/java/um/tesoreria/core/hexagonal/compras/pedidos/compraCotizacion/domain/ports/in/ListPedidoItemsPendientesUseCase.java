package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionPendiente;

import java.util.List;

/** Ítems de pedidos autorizados en presupuesto que aún no están en un proceso vigente. */
public interface ListPedidoItemsPendientesUseCase {

    List<CompraCotizacionPendiente> listar(Integer ejercicioId, List<Integer> dependenciaIds);
}
