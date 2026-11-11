package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.layout;

import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.DebitoPendiente;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Layout de Débito Directo: detalle de 600 posiciones + registro de control de 600.
 */
public final class DebitoDirectoLayout {

    private static final String EMPRESA_ID = "30518594466";
    private static final String SERVICIOS = "DEBITO";
    private static final String ENTIDAD_REGISTRANTE = "00720001";
    private static final String NOMBRE_EMPRESA = "UNIV DE MENDOZA";

    private DebitoDirectoLayout() {
    }

    public static String detalle(DebitoPendiente debito, BigDecimal importeCuota, String documentoId,
                                 String personaId, OffsetDateTime hoy, String hora) {
        String cbu = normalizarCbu(debito.getCbu());
        String clienteId = LayoutFormat.left(documentoId + personaId, 22);
        String digitos = cbu.length() >= 8 ? cbu.substring(7, 8) : " ";
        String cuenta = LayoutFormat.left("000" + LayoutFormat.right(cbu, 14), 17);
        String entidadDestino = LayoutFormat.left("0" + cbu, 8);
        long importeCentavos = LayoutFormat.centavos(importeCuota);

        return LayoutFormat.left(EMPRESA_ID, 11)
                + LayoutFormat.left(SERVICIOS, 10)
                + "37"
                + "00"
                + "00"
                + LayoutFormat.left(ENTIDAD_REGISTRANTE, 8)
                + entidadDestino
                + cuenta
                + clienteId
                + " ".repeat(15)
                + LayoutFormat.left(digitos, 1)
                + LayoutFormat.fecha(debito.getFechaVencimiento(), "yyyyMMdd")
                + " ".repeat(45)
                + LayoutFormat.zero(importeCentavos, 16)
                + LayoutFormat.left(String.valueOf(debito.getDebitoId()), 15)
                + " ".repeat(223)
                + LayoutFormat.fecha(hoy, "yyyyMMdd")
                + LayoutFormat.left(hora, 4)
                + " ".repeat(62)
                + LayoutFormat.left(NOMBRE_EMPRESA, 16)
                + " ".repeat(83)
                + LayoutFormat.fecha(hoy, "yyyyMMdd")
                + LayoutFormat.left(hora, 6)
                + " ".repeat(8);
    }

    public static String control(OffsetDateTime hoy, String hora, int registros, long totalCentavos) {
        StringBuilder control = new StringBuilder();
        control.append("9".repeat(21));
        control.append(" ".repeat(7));
        control.append(LayoutFormat.fecha(hoy, "yyyyMMdd"));
        control.append(LayoutFormat.left(hora, 6));
        control.append(" ".repeat(5));
        control.append("0".repeat(9).repeat(6));
        control.append(LayoutFormat.zero(registros, 9));
        control.append(LayoutFormat.zero(totalCentavos, 18));
        for (int i = 1; i < 12; i++) {
            control.append("0".repeat(27));
        }
        control.append(LayoutFormat.zero(registros + 1, 9));
        control.append(" ".repeat(144));
        control.append(LayoutFormat.fecha(hoy, "yyyyMMdd"));
        control.append(LayoutFormat.left(hora, 6));
        control.append(" ".repeat(8));
        return control.toString();
    }

    public static String filename(boolean soloSantander, boolean soloOtrosBancos, OffsetDateTime hoy) {
        String banco = "";
        if (soloSantander) {
            banco = "Santander.";
        }
        if (soloOtrosBancos) {
            banco = "OtrosBancos.";
        }
        return "DebitoAut." + banco + LayoutFormat.fecha(hoy, "ddMMyy");
    }

    public static String normalizarCbu(String cbu) {
        String limpio = cbu == null ? "" : cbu.trim();
        if (limpio.matches("\\d+")) {
            return String.format("%022d", new java.math.BigInteger(limpio));
        }
        return limpio;
    }

}
