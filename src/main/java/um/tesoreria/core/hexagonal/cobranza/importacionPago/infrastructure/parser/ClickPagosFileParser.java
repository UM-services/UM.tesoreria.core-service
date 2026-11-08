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
 * Parser del archivo de rendición de Click de Pagos.
 *
 * <p>Usa las mismas posiciones que {@code ClickPagosService.processLine} del core: registros
 * {@code HEADER}, {@code TRAILER} y {@code DATOS}. Del registro {@code DATOS} se toman las claves
 * de chequera (posiciones 515-529), el importe (78-88, dos decimales implícitos), la fecha de
 * liquidación (146-153) y la fecha de pago (225-230 con prefijo 20).</p>
 *
 * <p>El {@code tipoPagoId} se resuelve antes de construir la línea: 15 por defecto, 16 para
 * crédito ({@code formaPago = 80}) y 17 para debín ({@code formaPago = 60}). Ese orden corrige el
 * {@code ArB_TPa_ID = 0} del sistema anterior.</p>
 */
@Component
public class ClickPagosFileParser implements ParserArchivoPago {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final ZoneOffset ZONE = ZoneOffset.UTC;

    @Override
    public CanalImportacion canal() {
        return CanalImportacion.CLICK_PAGOS;
    }

    @Override
    public List<LineaImportacion> parsear(String filename, byte[] contenido) {
        String texto = new String(contenido, StandardCharsets.ISO_8859_1);
        List<LineaImportacion> lineas = new ArrayList<>();
        int numero = 0;
        for (String linea : texto.split("\\r?\\n")) {
            numero++;
            if (!linea.startsWith("DATOS") || linea.length() < 529) {
                continue;
            }
            LineaImportacion armada = parsearDatos(numero, linea);
            if (armada != null) {
                lineas.add(armada);
            }
        }
        return lineas;
    }

    private LineaImportacion parsearDatos(int numero, String linea) {
        try {
            Integer facultadId = entero(linea, 514, 516);
            Integer tipoChequeraId = entero(linea, 516, 519);
            Long chequeraSerieId = largo(linea, 519, 524);
            Integer alternativaId = entero(linea, 524, 526);
            Integer productoId = entero(linea, 526, 527);
            Integer cuotaId = entero(linea, 527, 529);
            if (facultadId == null || tipoChequeraId == null || chequeraSerieId == null
                    || alternativaId == null || productoId == null || cuotaId == null) {
                return null;
            }
            Integer formaPago = entero(linea, 247, 249);
            return LineaImportacion.builder()
                    .numeroLinea(numero)
                    .referencia(linea.substring(479, 529).trim())
                    .facultadId(facultadId)
                    .tipoChequeraId(tipoChequeraId)
                    .chequeraSerieId(chequeraSerieId)
                    .alternativaId(alternativaId)
                    .productoId(productoId)
                    .cuotaId(cuotaId)
                    .importe(importe(linea, 77, 88))
                    .fechaPago(fecha("20" + linea.substring(224, 230)))
                    .fechaAcreditacion(fecha(linea.substring(145, 153)))
                    .tipoPagoId(tipoPago(formaPago))
                    .build();
        } catch (RuntimeException e) {
            return null;
        }
    }

    private Integer tipoPago(Integer formaPago) {
        if (formaPago != null && formaPago == 80) {
            return 16;
        }
        if (formaPago != null && formaPago == 60) {
            return 17;
        }
        return 15;
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

    private OffsetDateTime fecha(String value) {
        String parte = value == null ? "" : value.trim();
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
