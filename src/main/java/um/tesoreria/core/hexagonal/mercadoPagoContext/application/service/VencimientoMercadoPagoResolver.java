package um.tesoreria.core.hexagonal.mercadoPagoContext.application.service;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.VencimientoMercadoPago;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Resuelve que vencimiento/importe de la cuota corresponde publicar en Mercado Pago.
 *
 * <p>Regla vigente: mientras {@code hoy <= vencimiento1} se publica el primer vencimiento;
 * luego el segundo y finalmente el tercero. Si los tres ya pasaron devuelve el tercero (el
 * llamador decide que hacer con fechas anteriores a hoy).</p>
 */
@Component
public class VencimientoMercadoPagoResolver {

    public VencimientoMercadoPago seleccionar(ChequeraCuota cuota, OffsetDateTime hoy) {
        if (hoy.isEqual(Objects.requireNonNull(cuota.getVencimiento1())) || hoy.isBefore(cuota.getVencimiento1())) {
            return new VencimientoMercadoPago(cuota.getVencimiento1(), cuota.getImporte1());
        }
        if (hoy.isEqual(Objects.requireNonNull(cuota.getVencimiento2())) || hoy.isBefore(cuota.getVencimiento2())) {
            return new VencimientoMercadoPago(cuota.getVencimiento2(), cuota.getImporte2());
        }
        return new VencimientoMercadoPago(cuota.getVencimiento3(), cuota.getImporte3());
    }

}
