package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.domain.model;

/**
 * Dependencia habilitada para que un usuario apruebe o rechace el envío de pedidos.
 */
public record CompraPedidoAutorizante(
        Integer autorizanteId,
        Integer dependenciaId) {

}
