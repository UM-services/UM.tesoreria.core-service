package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.parser;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.LineaImportacion;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClickPagosFileParserTest {

    private final ClickPagosFileParser parser = new ClickPagosFileParser();

    @Test
    void parseaUnRegistroDatosConLasPosicionesDelCore() {
        LineaImportacion linea = parser.parsear("click.txt",
                datos("00").getBytes(StandardCharsets.ISO_8859_1)).get(0);

        assertThat(linea.getNumeroLinea()).isEqualTo(1);
        assertThat(linea.getFacultadId()).isEqualTo(6);
        assertThat(linea.getTipoChequeraId()).isEqualTo(2);
        assertThat(linea.getChequeraSerieId()).isEqualTo(123L);
        assertThat(linea.getAlternativaId()).isEqualTo(1);
        assertThat(linea.getProductoId()).isEqualTo(1);
        assertThat(linea.getCuotaId()).isEqualTo(1);
        assertThat(linea.getImporte()).isEqualByComparingTo(new BigDecimal("123.45"));
        assertThat(linea.getFechaPago()).isEqualTo(OffsetDateTime.parse("2026-03-15T00:00:00Z"));
        assertThat(linea.getFechaAcreditacion()).isEqualTo(OffsetDateTime.parse("2026-03-20T00:00:00Z"));
        assertThat(linea.getTipoPagoId()).isEqualTo(15);
    }

    @Test
    void resuelveElTipoDePagoSegunLaFormaDePago() {
        assertThat(tipoPago("80")).isEqualTo(16);
        assertThat(tipoPago("60")).isEqualTo(17);
        assertThat(tipoPago("00")).isEqualTo(15);
    }

    @Test
    void ignoraHeaderTrailerYLineasCortas() {
        String contenido = "HEADER" + " ".repeat(100) + "\n"
                + "TRAILER" + " ".repeat(100) + "\n"
                + "DATOScorto" + "\n";

        assertThat(parser.parsear("click.txt", contenido.getBytes(StandardCharsets.ISO_8859_1))).isEmpty();
    }

    private int tipoPago(String formaPago) {
        return parser.parsear("click.txt", datos(formaPago).getBytes(StandardCharsets.ISO_8859_1))
                .get(0).getTipoPagoId();
    }

    private String datos(String formaPago) {
        char[] registro = new char[600];
        Arrays.fill(registro, ' ');
        set(registro, 1, "DATOS");
        set(registro, 78, "00000012345");
        set(registro, 146, "20260320");
        set(registro, 225, "260315");
        set(registro, 248, formaPago);
        set(registro, 480, "X".repeat(50));
        set(registro, 515, "06");
        set(registro, 517, "002");
        set(registro, 520, "00123");
        set(registro, 525, "01");
        set(registro, 527, "1");
        set(registro, 528, "01");
        return new String(registro);
    }

    private void set(char[] registro, int posicion, String valor) {
        for (int i = 0; i < valor.length(); i++) {
            registro[posicion - 1 + i] = valor.charAt(i);
        }
    }

}
