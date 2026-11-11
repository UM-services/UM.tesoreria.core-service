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
 * Parser del archivo de Débito Directo (CBU).
 *
 * <p>Detalle de 601 posiciones: {@code codigoT} en 22-23 ({@code 37} debitado, {@code 36}
 * rechazado), importe en 152-167, {@code refUnivocaDeb} en 168-182 (es el {@code debitoId}) y
 * motivo de rechazo en 209-296. Se tolera terminación {@code LF} o {@code CRLF} y nunca se
 * reescribe el archivo.</p>
 */
@Component
public class DebitoDirectoFileParser implements ParserArchivoPago {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final ZoneOffset ZONE = ZoneOffset.UTC;
    private static final int TIPO_PAGO = 5;
    private static final String MOTIVO_DESCONOCIDO = "RECHAZO MOTIVO DESCONOCIDO";

    @Override
    public CanalImportacion canal() {
        return CanalImportacion.DEBITO_DIRECTO;
    }

    @Override
    public List<LineaImportacion> parsear(String filename, byte[] contenido) {
        String texto = new String(contenido, StandardCharsets.ISO_8859_1);
        List<LineaImportacion> lineas = new ArrayList<>();
        int numero = 0;
        for (String linea : texto.split("\\r?\\n")) {
            numero++;
            if (linea.trim().isEmpty() || linea.charAt(0) != '3' || linea.length() < 168) {
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
        String referencia = sub(linea, 168, 182);
        if (referencia.isEmpty()) {
            return null;
        }
        Long debitoId;
        try {
            debitoId = Long.valueOf(referencia);
        } catch (NumberFormatException e) {
            return null;
        }
        String codigoTipoRegistro = sub(linea, 22, 23);
        boolean rechazado = !"37".equals(codigoTipoRegistro);
        String motivo = rechazado ? sub(linea, 209, 296) : "";
        if (rechazado && motivo.isEmpty()) {
            motivo = MOTIVO_DESCONOCIDO;
        }
        OffsetDateTime fecha = fecha(linea, 99, 106);
        return LineaImportacion.builder()
                .numeroLinea(numero)
                .referencia(referencia)
                .debitoId(debitoId)
                .codigoTipoRegistro(codigoTipoRegistro)
                .rechazado(rechazado)
                .motivoRechazo(motivo)
                .importe(importe(linea, 152, 167))
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
