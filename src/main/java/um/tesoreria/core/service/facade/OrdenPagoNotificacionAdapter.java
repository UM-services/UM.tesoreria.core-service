package um.tesoreria.core.service.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.out.NotificacionOrdenPagoPort;

/**
 * Adaptador del puerto de notificación del slice {@code pagos/ordenPago}.
 * Vive en el paquete legacy porque es el legacy el que puede depender de los puertos
 * hexagonales; el slice no puede depender del legacy.
 */
@Component
@RequiredArgsConstructor
public class OrdenPagoNotificacionAdapter implements NotificacionOrdenPagoPort {

    private final NotificacionService notificacionService;

    @Override
    public void notificar(Long proveedorMovimientoId) {
        notificacionService.notifyPagoProveedor(proveedorMovimientoId);
    }
}
