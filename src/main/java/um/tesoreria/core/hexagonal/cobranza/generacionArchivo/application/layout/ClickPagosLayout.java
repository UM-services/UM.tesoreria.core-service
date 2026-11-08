package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.layout;

import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.CuotaDeuda;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Layout de Click de Pagos: empresa 791893, header/trailer de 282 y total de 16 posiciones.
 */
public final class ClickPagosLayout {

    private static final String EMPRESA = "791893";

    private ClickPagosLayout() {
    }

    public static String header(OffsetDateTime fecha) {
        return "0" + "400" + EMPRESA + LayoutFormat.fecha(fecha, "yyyyMMdd") + "0".repeat(264);
    }

    public static String detalle(CuotaDeuda cuota) {
        return FacturacionLayout.detalle(cuota);
    }

    public static String trailer(OffsetDateTime fecha, int registros, BigDecimal total) {
        return "9" + "400" + EMPRESA + LayoutFormat.fecha(fecha, "yyyyMMdd")
                + LayoutFormat.zero(registros, 7) + "0".repeat(7)
                + LayoutFormat.importe(total, 14, 2) + "0".repeat(234);
    }

}
