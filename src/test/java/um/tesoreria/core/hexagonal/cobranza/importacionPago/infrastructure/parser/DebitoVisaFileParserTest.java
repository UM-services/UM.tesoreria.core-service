package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.parser;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.LineaImportacion;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DebitoVisaFileParserTest {

    private final DebitoVisaFileParser parser = new DebitoVisaFileParser();

    @Test
    void parseaUnDetalleSinRechazo() {
        List<LineaImportacion> lineas = parser.parsear("visa.txt", archivo(
                detalle("00", "", "00", "")));

        assertThat(lineas).hasSize(1);
        LineaImportacion linea = lineas.get(0);
        assertThat(linea.getFacultadId()).isEqualTo(6);
        assertThat(linea.getTipoChequeraId()).isEqualTo(2);
        assertThat(linea.getChequeraSerieId()).isEqualTo(123L);
        assertThat(linea.getProductoId()).isEqualTo(1);
        assertThat(linea.getAlternativaId()).isEqualTo(1);
        assertThat(linea.getCuotaId()).isEqualTo(1);
        assertThat(linea.getNumeroCupon()).isEqualTo("00010101");
        assertThat(linea.getImporte()).isEqualByComparingTo(new BigDecimal("123.45"));
        assertThat(linea.getFechaPago()).isEqualTo(OffsetDateTime.parse("2026-03-15T00:00:00Z"));
        assertThat(linea.isRechazado()).isFalse();
        assertThat(linea.getTipoPagoId()).isEqualTo(6);
    }

    @Test
    void parseaUnRechazoPorElPrimerCodigo() {
        LineaImportacion linea = parser.parsear("visa.txt", archivo(
                detalle("01", "TARJETA VENCIDA", "00", ""))).get(0);

        assertThat(linea.isRechazado()).isTrue();
        assertThat(linea.getMotivoRechazo()).isEqualTo("TARJETA VENCIDA");
    }

    @Test
    void parseaUnRechazoPorElSegundoCodigo() {
        LineaImportacion linea = parser.parsear("visa.txt", archivo(
                detalle("00", "", "02", "SIN CUPO"))).get(0);

        assertThat(linea.isRechazado()).isTrue();
        assertThat(linea.getMotivoRechazo()).isEqualTo("SIN CUPO");
    }

    @Test
    void completaElMotivoCuandoElArchivoNoLoTre() {
        LineaImportacion linea = parser.parsear("visa.txt", archivo(
                detalle("01", "", "00", ""))).get(0);

        assertThat(linea.getMotivoRechazo()).isEqualTo("RECHAZO MOTIVO DESCONOCIDO");
    }

    @Test
    void toleraLfYCrLfSinReescribirElArchivo() {
        String lf = String.join("\n", detalle("00", "", "00", ""),
                detalle("01", "TARJETA VENCIDA", "00", "")) + "\n";
        String crlf = lf.replace("\n", "\r\n");

        List<LineaImportacion> conLf = parser.parsear("visa.txt", lf.getBytes(StandardCharsets.ISO_8859_1));
        List<LineaImportacion> conCrLf = parser.parsear("visa.txt", crlf.getBytes(StandardCharsets.ISO_8859_1));

        assertThat(conLf).usingRecursiveComparison().isEqualTo(conCrLf);
        assertThat(conLf).hasSize(2);
    }

    @Test
    void noModificaElArchivoRecibido() {
        byte[] contenido = archivo(detalle("00", "", "00", ""));
        byte[] copia = contenido.clone();

        parser.parsear("visa.txt", contenido);

        assertThat(contenido).isEqualTo(copia);
    }

    @Test
    void ignoraHeaderTrailerYLineasCortas() {
        String contenido = "0" + " ".repeat(200) + "\n"
                + "1corta\n"
                + "9" + " ".repeat(200) + "\n";

        assertThat(parser.parsear("visa.txt", contenido.getBytes(StandardCharsets.ISO_8859_1))).isEmpty();
    }

    private byte[] archivo(String... lineas) {
        return (String.join("\n", lineas) + "\n").getBytes(StandardCharsets.ISO_8859_1);
    }

    private String detalle(String codRechazo1, String descRechazo1, String codRechazo2, String descRechazo2) {
        char[] registro = new char[300];
        Arrays.fill(registro, ' ');
        set(registro, 1, "1");
        set(registro, 43, "00010101");
        set(registro, 63, "000000000012345");
        set(registro, 95, "000000600200123");
        set(registro, 131, codRechazo1);
        set(registro, 133, descRechazo1);
        set(registro, 162, codRechazo2);
        set(registro, 164, descRechazo2);
        set(registro, 225, "150326");
        return new String(registro);
    }

    private void set(char[] registro, int posicion, String valor) {
        for (int i = 0; i < valor.length(); i++) {
            registro[posicion - 1 + i] = valor.charAt(i);
        }
    }

}
