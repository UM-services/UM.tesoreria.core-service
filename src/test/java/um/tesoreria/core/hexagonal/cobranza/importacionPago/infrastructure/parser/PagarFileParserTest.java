package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.parser;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.LineaImportacion;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PagarFileParserTest {

    private final PagarFileParser parser = new PagarFileParser();

    @Test
    void parseaElDatoYUsaLaFechaDeProcesoComoAcreditacion() {
        String contenido = inicial("20260125")
                + dato("01101", "0600200123", "000000012345", "20260120")
                + final_();

        List<LineaImportacion> lineas = parser.parsear("pagar.txt", contenido.getBytes(StandardCharsets.ISO_8859_1));

        assertThat(lineas).hasSize(1);
        LineaImportacion linea = lineas.get(0);
        assertThat(linea.getFacultadId()).isEqualTo(6);
        assertThat(linea.getTipoChequeraId()).isEqualTo(2);
        assertThat(linea.getChequeraSerieId()).isEqualTo(123L);
        assertThat(linea.getAlternativaId()).isEqualTo(1);
        assertThat(linea.getProductoId()).isEqualTo(1);
        assertThat(linea.getCuotaId()).isEqualTo(1);
        assertThat(linea.getImporte()).isEqualByComparingTo(new BigDecimal("123.45"));
        assertThat(linea.getFechaPago()).isEqualTo(OffsetDateTime.parse("2026-01-20T00:00:00Z"));
        assertThat(linea.getFechaAcreditacion()).isEqualTo(OffsetDateTime.parse("2026-01-25T00:00:00Z"));
        assertThat(linea.getTipoPagoId()).isEqualTo(13);
    }

    @Test
    void ignoraRegistrosDesconocidosYLineasCortas() {
        String contenido = "9\n" + "1corta\n" + final_();

        assertThat(parser.parsear("pagar.txt", contenido.getBytes(StandardCharsets.ISO_8859_1))).isEmpty();
    }

    private String inicial(String fechaProceso) {
        return "0" + "   " + fechaProceso + " ".repeat(20) + "\n";
    }

    private String dato(String deuda, String usuario, String importe, String fechaPago) {
        return "1"
                + deuda
                + "001"
                + String.format("%-19s", usuario)
                + importe
                + fechaPago
                + " ".repeat(20)
                + "\n";
    }

    private String final_() {
        return "2" + " ".repeat(40) + "\n";
    }

}
