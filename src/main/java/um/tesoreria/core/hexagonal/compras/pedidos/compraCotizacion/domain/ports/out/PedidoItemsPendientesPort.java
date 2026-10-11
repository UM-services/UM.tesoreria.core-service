package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionPendiente;

import java.util.List;

public interface PedidoItemsPendientesPort {

    List<CompraCotizacionPendiente> listar(Integer ejercicioId, List<Integer> dependenciaIds);
}
