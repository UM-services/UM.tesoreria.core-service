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
 * Parser del archivo de rendición de Rapipago.
 *
 * <p>Formato de ancho fijo: el tipo de registro está en la posición 23 ({@code C} cabecera,
 * {@code T} trailer, {@code D} detalle) y el subtipo del detalle en la 43 ({@code 0} pago,
 * {@code 1} instrumento, {@code 2} comprobante). Cada grupo se arma con el subregistro 0 y se
 * completa con el 1; el 2 no participa de la imputación. La barra viaja en las posiciones
 * 248-347 y el verificador en la 44 de la barra, igual que en el sistema anterior.</p>
 */
@Component
public class RapipagoFileParser implements ParserArchivoPago {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final ZoneOffset ZONE = ZoneOffset.UTC;
    private static final int TIPO_PAGO = 3;

    @Override
    public CanalImportacion canal() {
        return CanalImportacion.RAPIPAGO;
    }

    @Override
    public List<LineaImportacion> parsear(String filename, byte[] contenido) {
        String texto = new String(contenido, StandardCharsets.ISO_8859_1);
        List<LineaImportacion> lineas = new ArrayList<>();
        Acumulador acumulador = null;
        int numero = 0;
        for (String linea : texto.split("\\r?\\n")) {
            numero++;
            if (linea.length() < 43 || caracter(linea, 23) != 'D') {
                continue;
            }
            char subtipo = caracter(linea, 43);
            if (subtipo == '0') {
                acumulador = new Acumulador(numero, linea);
            } else if (subtipo == '1' && acumulador != null) {
                LineaImportacion armada = acumulador.completar(linea);
                if (armada != null) {
                    lineas.add(armada);
                }
                acumulador = null;
            }
            // El subregistro 2 (comprobante) no se usa para imputar.
        }
        return lineas;
    }

    private char caracter(String linea, int posicion) {
        return linea.length() >= posicion ? linea.charAt(posicion - 1) : ' ';
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

    /** Barra de Rapipago (100 posiciones) con las claves de la chequera. */
    private Barra parsearBarra(String barra) {
        if (barra == null || barra.length() < 44) {
            return null;
        }
        Integer facultadId = entero(barra, 4, 5);
        Integer tipoChequeraId = entero(barra, 6, 8);
        Long chequeraSerieId = largo(barra, 9, 13);
        Integer productoId = entero(barra, 14, 14);
        Integer mes = entero(barra, 15, 16);
        Integer anhoCorto = entero(barra, 17, 18);
        Integer cuotaId = entero(barra, 19, 20);
        String verificador = sub(barra, 44, 44);
        if (facultadId == null || tipoChequeraId == null || chequeraSerieId == null
                || productoId == null || cuotaId == null) {
            return null;
        }
        return new Barra(facultadId, tipoChequeraId, chequeraSerieId, productoId, mes,
                anhoCorto == null ? null : 2000 + anhoCorto, cuotaId, verificador);
    }

    private final class Acumulador {

        private final int numeroLinea;
        private final String dato0;

        private Acumulador(int numeroLinea, String dato0) {
            this.numeroLinea = numeroLinea;
            this.dato0 = dato0;
        }

        private LineaImportacion completar(String dato1) {
            Barra barra = parsearBarra(sub(dato0, 248, 347));
            if (barra == null) {
                return null;
            }
            return LineaImportacion.builder()
                    .numeroLinea(numeroLinea)
                    .referencia(sub(dato0, 248, 347))
                    .facultadId(barra.facultadId())
                    .tipoChequeraId(barra.tipoChequeraId())
                    .chequeraSerieId(barra.chequeraSerieId())
                    .productoId(barra.productoId())
                    .mes(barra.mes())
                    .anho(barra.anho())
                    .cuotaId(barra.cuotaId())
                    .verificador(barra.verificador())
                    .importe(importe(dato1, 83, 97))
                    .fechaPago(fecha(dato0, 107, 114))
                    .fechaAcreditacion(fecha(dato1, 98, 105))
                    .marcaAcreditacion(sub(dato1, 106, 106))
                    .tipoPagoId(TIPO_PAGO)
                    .build();
        }

    }

    private record Barra(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                         Integer productoId, Integer mes, Integer anho, Integer cuotaId,
                         String verificador) {
    }

}
