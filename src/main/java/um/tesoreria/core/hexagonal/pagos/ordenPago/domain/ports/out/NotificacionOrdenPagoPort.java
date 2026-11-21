package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.out;

/**
 * Notificación de la orden de pago al proveedor. La implementación vive fuera del slice
 * (el legacy puede depender de los puertos hexagonales, no al revés).
 */
public interface NotificacionOrdenPagoPort {

    void notificar(Long proveedorMovimientoId);
}
