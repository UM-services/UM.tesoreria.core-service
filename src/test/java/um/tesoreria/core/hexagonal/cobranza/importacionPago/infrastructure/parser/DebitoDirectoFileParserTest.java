package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.parser;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.LineaImportacion;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DebitoDirectoFileParserTest {

    private final DebitoDirectoFileParser parser = new DebitoDirectoFileParser();

    @Test
    void parseaUnDebitoAcreditado() {
        List<LineaImportacion> lineas = parser.parsear("debito.txt", archivo(
                detalle("37", "0000000000012345", "000000000000123", "SIN SALDO")));

        assertThat(lineas).hasSize(1);
        LineaImportacion linea = lineas.get(0);
        assertThat(linea.getNumeroLinea()).isEqualTo(1);
        assertThat(linea.getDebitoId()).isEqualTo(123L);
        assertThat(linea.getReferencia()).isEqualTo("000000000000123");
        assertThat(linea.getCodigoTipoRegistro()).isEqualTo("37");
        assertThat(linea.isRechazado()).isFalse();
        assertThat(linea.getMotivoRechazo()).isEmpty();
        assertThat(linea.getImporte()).isEqualByComparingTo(new BigDecimal("123.45"));
        assertThat(linea.getFechaPago()).isEqualTo(OffsetDateTime.parse("2026-03-15T00:00:00Z"));
        assertThat(linea.getTipoPagoId()).isEqualTo(5);
    }

    @Test
    void parseaUnRechazoConMotivo() {
        LineaImportacion linea = parser.parsear("debito.txt", archivo(
                detalle("36", "0000000000012345", "000000000000124", "SIN SALDO"))).get(0);

        assertThat(linea.isRechazado()).isTrue();
        assertThat(linea.getMotivoRechazo()).isEqualTo("SIN SALDO");
        assertThat(linea.getDebitoId()).isEqualTo(124L);
    }

    @Test
    void completaElMotivoCuandoElArchivoNoLoTre() {
        LineaImportacion linea = parser.parsear("debito.txt", archivo(
                detalle("36", "0000000000012345", "000000000000125", ""))).get(0);

        assertThat(linea.isRechazado()).isTrue();
        assertThat(linea.getMotivoRechazo()).isEqualTo("RECHAZO MOTIVO DESCONOCIDO");
    }

    @Test
    void toleraLfYCrLfSinReescribirElArchivo() {
        String lf = String.join("\n",
                detalle("37", "0000000000012345", "000000000000123", ""),
                detalle("36", "0000000000067890", "000000000000124", "SIN SALDO")) + "\n";
        String crlf = lf.replace("\n", "\r\n");

        List<LineaImportacion> conLf = parser.parsear("debito.txt", lf.getBytes(StandardCharsets.ISO_8859_1));
        List<LineaImportacion> conCrLf = parser.parsear("debito.txt", crlf.getBytes(StandardCharsets.ISO_8859_1));

        assertThat(conLf).usingRecursiveComparison().isEqualTo(conCrLf);
        assertThat(conLf).hasSize(2);
    }

    @Test
    void noModificaElArchivoRecibido() {
        byte[] contenido = archivo(detalle("37", "0000000000012345", "000000000000123", ""));
        byte[] copia = contenido.clone();

        parser.parsear("debito.txt", contenido);

        assertThat(contenido).isEqualTo(copia);
    }

    @Test
    void ignoraTrailerYLineasCortas() {
        String contenido = "9" + " ".repeat(200) + "\n"
                + "3corta\n"
                + "0" + " ".repeat(200) + "\n";

        assertThat(parser.parsear("debito.txt", contenido.getBytes(StandardCharsets.ISO_8859_1))).isEmpty();
    }

    private byte[] archivo(String... lineas) {
        return (String.join("\n", lineas) + "\n").getBytes(StandardCharsets.ISO_8859_1);
    }

    private String detalle(String codigoT, String importe, String debitoId, String motivo) {
        char[] registro = new char[601];
        Arrays.fill(registro, ' ');
        set(registro, 1, "3");
        set(registro, 22, codigoT);
        set(registro, 99, "20260315");
        set(registro, 152, importe);
        set(registro, 168, debitoId);
        set(registro, 209, motivo);
        return new String(registro);
    }

    private void set(char[] registro, int posicion, String valor) {
        for (int i = 0; i < valor.length(); i++) {
            registro[posicion - 1 + i] = valor.charAt(i);
        }
    }

}
