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
 * Parser del archivo de rendición PAGAR.
 *
 * <p>Registros: {@code 0} inicial (trae la fecha de proceso, que se usa como acreditación),
 * {@code 1} dato y {@code 2} final. El identificador de deuda (alt+pro+cuo) y el de usuario
 * (fac+tch+chs) vienen explícitos, por lo que no hace falta resolver barras ni cupones.</p>
 */
@Component
public class PagarFileParser implements ParserArchivoPago {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final ZoneOffset ZONE = ZoneOffset.UTC;

    @Override
    public CanalImportacion canal() {
        return CanalImportacion.PAGAR;
    }

    @Override
    public List<LineaImportacion> parsear(String filename, byte[] contenido) {
        String texto = new String(contenido, StandardCharsets.ISO_8859_1);
        OffsetDateTime fechaProceso = null;
        List<LineaImportacion> lineas = new ArrayList<>();
        int numero = 0;
        for (String linea : texto.split("\\r?\\n")) {
            numero++;
            if (linea.length() < 8) {
                continue;
            }
            switch (linea.charAt(0)) {
                case '0' -> fechaProceso = fecha(linea, 4, 12);
                case '1' -> {
                    if (linea.length() < 48) {
                        continue;
                    }
                    lineas.add(LineaImportacion.builder()
                            .numeroLinea(numero)
                            .referencia(linea.substring(1, 28).trim())
                            .alternativaId(entero(linea, 1, 3))
                            .productoId(entero(linea, 3, 4))
                            .cuotaId(entero(linea, 4, 6))
                            .facultadId(entero(linea, 9, 11))
                            .tipoChequeraId(entero(linea, 11, 14))
                            .chequeraSerieId(largo(linea, 14, 19))
                            .importe(importe(linea, 28, 40))
                            .fechaPago(fecha(linea, 40, 48))
                            .fechaAcreditacion(fechaProceso)
                            .tipoPagoId(13)
                            .build());
                }
                default -> {
                    // registros 2 (final) y cualquier línea desconocida se ignoran
                }
            }
        }
        return lineas;
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
        if (value.length() < hasta) {
            return null;
        }
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
