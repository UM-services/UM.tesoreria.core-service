package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.layout;

import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.DebitoPendiente;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

/**
 * Layout de Débito VISA: header/detalle/pie de 100 posiciones delimitados por {@code *}.
 */
public final class DebitoVisaLayout {

    private static final String CONSTANTE = "DEBLIQC ";
    private static final String ESTABLECIMIENTO = "0024054728";
    private static final String DESTINO = "900000    ";

    private DebitoVisaLayout() {
    }

    public static String header(OffsetDateTime hoy, String hora) {
        return "0"
                + LayoutFormat.left(CONSTANTE, 8)
                + LayoutFormat.left(ESTABLECIMIENTO, 10)
                + LayoutFormat.left(DESTINO, 10)
                + LayoutFormat.fecha(hoy, "yyyyMMdd")
                + LayoutFormat.left(hora, 4)
                + "0"
                + " ".repeat(2)
                + " ".repeat(55)
                + "*";
    }

    public static String detalle(DebitoPendiente debito, BigDecimal importeCuota, Integer claseChequeraId,
                                 BigDecimal descuento, boolean primerEnvio) {
        long importe = importeConDescuento(importeCuota, claseChequeraId, descuento);
        String nroComprobante = LayoutFormat.zero(
                Long.parseLong(String.format("%01d%02d%02d",
                        nvl(debito.getProductoId()), nvl(debito.getAlternativaId()), nvl(debito.getCuotaId()))), 8);
        String debitoId = LayoutFormat.zero(Long.parseLong(String.format("%02d%03d%05d",
                nvl(debito.getFacultadId()), nvl(debito.getTipoChequeraId()), nvlLong(debito.getChequeraSerieId()))), 15);
        String tarjeta = LayoutFormat.left(debito.getNumeroTarjeta(), 16);

        return "1"
                + tarjeta
                + " ".repeat(3)
                + nroComprobante
                + LayoutFormat.fecha(debito.getFechaVencimiento(), "yyyyMMdd")
                + "0005"
                + LayoutFormat.zero(importe, 13) + "00"
                + debitoId
                + (primerEnvio ? "E" : " ")
                + " ".repeat(2)
                + " ".repeat(26)
                + "*";
    }

    public static String trailer(OffsetDateTime hoy, String hora, int registros, long totalImporte) {
        return "9"
                + LayoutFormat.left(CONSTANTE, 8)
                + LayoutFormat.left(ESTABLECIMIENTO, 10)
                + LayoutFormat.left(DESTINO, 10)
                + LayoutFormat.fecha(hoy, "yyyyMMdd")
                + LayoutFormat.left(hora, 4)
                + LayoutFormat.zero(registros, 7)
                + LayoutFormat.zero(totalImporte, 13) + "00"
                + " ".repeat(36)
                + "*";
    }

    public static long importeConDescuento(BigDecimal importeCuota, Integer claseChequeraId, BigDecimal descuento) {
        BigDecimal importe = importeCuota == null ? BigDecimal.ZERO : importeCuota;
        if (claseChequeraId != null && claseChequeraId == 2) {
            BigDecimal factor = BigDecimal.valueOf(100).subtract(descuento == null ? BigDecimal.ZERO : descuento);
            importe = importe.multiply(factor).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        }
        return LayoutFormat.entero(importe);
    }

    private static Integer nvl(Integer value) {
        return value == null ? 0 : value;
    }

    private static Long nvlLong(Long value) {
        return value == null ? 0L : value;
    }

}
