package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.parser;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.CanalImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.LineaImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ParserArchivoPago;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser del archivo de Débito VISA.
 *
 * <p>Detalle de 300 posiciones: cupón en 43-50, importe en 63-77, id en 95-109, códigos de rechazo
 * en 131-132 y 162-163, descripciones en 133-161 y 164-192 y fecha de presentación en 225-230
 * ({@code ddMMaa}). Las claves de chequera se derivan del id y del cupón, igual que en el sistema
 * anterior.</p>
 */
@Component
public class DebitoVisaFileParser implements ParserArchivoPago {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("ddMMyy");
    private static final ZoneOffset ZONE = ZoneOffset.UTC;
    private static final int TIPO_PAGO = 6;
    private static final String MOTIVO_DESCONOCIDO = "RECHAZO MOTIVO DESCONOCIDO";

    @Override
    public CanalImportacion canal() {
        return CanalImportacion.DEBITO_VISA;
    }

    @Override
    public List<LineaImportacion> parsear(String filename, byte[] contenido) {
        String texto = new String(contenido, StandardCharsets.ISO_8859_1);
        List<LineaImportacion> lineas = new ArrayList<>();
        int numero = 0;
        for (String linea : texto.split("\\r?\\n")) {
            numero++;
            if (linea.trim().isEmpty() || linea.charAt(0) != '1' || linea.length() < 231) {
                continue;
            }
            LineaImportacion armada = parsearDetalle(numero, linea);
            if (armada != null) {
                lineas.add(armada);
            }
        }
        return lineas;
    }

    private LineaImportacion parsearDetalle(int numero, String linea) {
        String id = sub(linea, 95, 109);
        String cupon = sub(linea, 43, 50);
        if (id.length() < 15 || cupon.length() < 8) {
            return null;
        }
        Integer facultadId = entero(id, 6, 7);
        Integer tipoChequeraId = entero(id, 8, 10);
        Long chequeraSerieId = largo(id, 11, 15);
        Integer productoId = entero(cupon, 4, 4);
        Integer alternativaId = entero(cupon, 5, 6);
        Integer cuotaId = entero(cupon, 7, 8);
        if (facultadId == null || tipoChequeraId == null || chequeraSerieId == null
                || productoId == null || alternativaId == null || cuotaId == null) {
            return null;
        }

        Integer codRechazo1 = entero(linea, 131, 132);
        Integer codRechazo2 = entero(linea, 162, 163);
        boolean rechazado = (codRechazo1 != null && codRechazo1 != 0)
                || (codRechazo2 != null && codRechazo2 != 0);
        String motivo = rechazado
                ? (sub(linea, 133, 161) + " " + sub(linea, 164, 192)).trim()
                : "";
        if (rechazado && motivo.isEmpty()) {
            motivo = MOTIVO_DESCONOCIDO;
        }

        OffsetDateTime fecha = fecha(linea, 225, 230);
        return LineaImportacion.builder()
                .numeroLinea(numero)
                .referencia(id)
                .numeroCupon(cupon)
                .facultadId(facultadId)
                .tipoChequeraId(tipoChequeraId)
                .chequeraSerieId(chequeraSerieId)
                .productoId(productoId)
                .alternativaId(alternativaId)
                .cuotaId(cuotaId)
                .rechazado(rechazado)
                .motivoRechazo(motivo)
                .importe(importe(linea, 63, 77))
                .fechaPago(fecha)
                .fechaAcreditacion(fecha)
                .tipoPagoId(TIPO_PAGO)
                .build();
    }

    private String sub(String value, int desde, int hasta) {
        if (value == null || value.length() < desde) {
            return "";
        }
        int fin = Math.min(hasta, value.length());
        return value.substring(desde - 1, fin).trim();
    }

    private Integer entero(String value, int desde, int hasta) {
        String parte = sub(value, desde, hasta);
        return parte.isEmpty() ? null : Integer.valueOf(parte);
    }

    private Long largo(String value, int desde, int hasta) {
        String parte = sub(value, desde, hasta);
        return parte.isEmpty() ? null : Long.valueOf(parte);
    }

    private BigDecimal importe(String value, int desde, int hasta) {
        String parte = sub(value, desde, hasta);
        return parte.isEmpty() ? BigDecimal.ZERO : new BigDecimal(parte).movePointLeft(2);
    }

    private OffsetDateTime fecha(String value, int desde, int hasta) {
        String parte = sub(value, desde, hasta);
        if (parte.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(parte, DATE).atStartOfDay().atOffset(ZONE);
        } catch (RuntimeException e) {
            return null;
        }
    }

}
