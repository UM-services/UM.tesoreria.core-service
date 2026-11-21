package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.util.List;

/**
 * Resultado del cálculo de reconstrucción de traza de una orden de pago: lo que hay que crear,
 * lo que hay que completar y los errores detectados al armar las líneas propuestas.
 */
public record ReconstruccionPlan(Long proveedorMovimientoId, Integer prefijo, Long numeroComprobante,
                                 List<ReconstruccionAsiento> asientos, int cantidadCrear,
                                 int cantidadActualizar, int cantidadSinCambios, boolean requiereForzar,
                                 List<String> errores) {

    public boolean sinCambios() {
        return cantidadCrear == 0 && cantidadActualizar == 0;
    }
}
