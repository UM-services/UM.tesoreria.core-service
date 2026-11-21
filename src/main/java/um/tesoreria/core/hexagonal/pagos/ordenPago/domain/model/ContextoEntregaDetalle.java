package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;

public record ContextoEntregaDetalle(Integer ubicacionId, Long articuloId, BigDecimal cantidad,
                                     BigDecimal numeroCuentaArticulo, BigDecimal numeroCuentaUbicacion,
                                     String nombreUbicacion, String nombreArticulo) {

    /** El legacy usa el nombre compuesto cuando la cuenta de la ubicación no está configurada. */
    public boolean sinCuentaUbicacion() {
        return numeroCuentaUbicacion == null || numeroCuentaUbicacion.signum() == 0;
    }
}
