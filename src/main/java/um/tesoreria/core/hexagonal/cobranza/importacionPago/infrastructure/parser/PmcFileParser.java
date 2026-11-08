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
 * Parser del archivo de rendición de PagoMisCuentas (formato Banelco de ancho fijo).
 *
 * <p>Registro de detalle: tipo {@code 5}; {@code idFactura} en las posiciones 21-40; importe en
 * 58-68 con dos decimales implícitos; fechas {@code yyyyMMdd}. Se conservan las posiciones del
 * layout histórico; cualquier cambio de formato debe validarse contra un archivo real.</p>
 */
@Component
public class PmcFileParser implements ParserArchivoPago {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final ZoneOffset ZONE = ZoneOffset.UTC;

    @Override
    public CanalImportacion canal() {
        return CanalImportacion.PMC;
    }

    @Override
    public List<LineaImportacion> parsear(String filename, byte[] contenido) {
        String texto = new String(contenido, StandardCharsets.ISO_8859_1);
        int tipoPagoId = tipoPago(filename);
        List<LineaImportacion> lineas = new ArrayList<>();
        int numero = 0;
        for (String linea : texto.split("\\r?\\n")) {
            numero++;
            if (linea.length() < 68 || linea.charAt(0) != '5') {
                continue;
            }
            String idFactura = linea.substring(20, 40).trim();
            if (idFactura.length() < 15) {
                continue;
            }
            lineas.add(LineaImportacion.builder()
                    .numeroLinea(numero)
                    .referencia(idFactura)
                    .facultadId(entero(idFactura, 0, 2))
                    .tipoChequeraId(entero(idFactura, 2, 5))
                    .chequeraSerieId(largo(idFactura, 5, 10))
                    .alternativaId(entero(idFactura, 10, 12))
                    .productoId(entero(idFactura, 12, 13))
                    .cuotaId(entero(idFactura, 13, 15))
                    .fechaPago(fecha(linea, 49, 57))
                    .importe(importe(linea, 57, 68))
                    .fechaAcreditacion(fecha(linea, 69, 77))
                    .tipoPagoId(tipoPagoId)
                    .build());
        }
        return lineas;
    }

    private int tipoPago(String filename) {
        String nombre = filename == null ? "" : filename.toLowerCase();
        if (nombre.startsWith("cobd")) {
            return 11;
        }
        if (nombre.startsWith("cobv")) {
            return 12;
        }
        return 4;
    }

    private Integer entero(String value, int desde, int hasta) {
        String parte = value.substring(desde, hasta).trim();
        return parte.isEmpty() ? null : Integer.valueOf(parte);
    }

    private Long largo(String value, int desde, int hasta) {
        String parte = value.substring(desde, hasta).trim();
        return parte.isEmpty() ? null : Long.valueOf(parte);
    }

    private BigDecimal importe(String value, int desde, int hasta) {
        String parte = value.substring(desde, hasta).trim();
        return parte.isEmpty() ? BigDecimal.ZERO : new BigDecimal(parte).movePointLeft(2);
    }

    private OffsetDateTime fecha(String value, int desde, int hasta) {
        String parte = value.substring(desde, hasta).trim();
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
