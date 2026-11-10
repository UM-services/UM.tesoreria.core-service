package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto;

import java.util.List;

/** Filtro de la bandeja de ítems pendientes de cotizar: ejercicio y dependencias. */
public record CompraCotizacionPendientesRequest(
        Integer ejercicioId,
        List<Integer> dependenciaIds) {

}
