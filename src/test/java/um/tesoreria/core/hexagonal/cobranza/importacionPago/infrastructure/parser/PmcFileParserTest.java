package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.parser;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.LineaImportacion;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PmcFileParserTest {

    private final PmcFileParser parser = new PmcFileParser();

    @Test
    void parseaUnDetalleConLasPosicionesHistoricas() {
        List<LineaImportacion> lineas = parser.parsear("cobc20260120.txt",
                detalle("060020012301101", "20260120", "00000012345", "20260122"));

        assertThat(lineas).hasSize(1);
        LineaImportacion linea = lineas.get(0);
        assertThat(linea.getNumeroLinea()).isEqualTo(1);
        assertThat(linea.getFacultadId()).isEqualTo(6);
        assertThat(linea.getTipoChequeraId()).isEqualTo(2);
        assertThat(linea.getChequeraSerieId()).isEqualTo(123L);
        assertThat(linea.getAlternativaId()).isEqualTo(1);
        assertThat(linea.getProductoId()).isEqualTo(1);
        assertThat(linea.getCuotaId()).isEqualTo(1);
        assertThat(linea.getImporte()).isEqualByComparingTo(new BigDecimal("123.45"));
        assertThat(linea.getFechaPago()).isEqualTo(OffsetDateTime.parse("2026-01-20T00:00:00Z"));
        assertThat(linea.getFechaAcreditacion()).isEqualTo(OffsetDateTime.parse("2026-01-22T00:00:00Z"));
        assertThat(linea.getTipoPagoId()).isEqualTo(4);
    }

    @Test
    void deduceElTipoDePagoDelPrefijoDelNombre() {
        assertThat(parser.parsear("cobd20260120.txt",
                detalle("060020012301101", "20260120", "00000012345", "20260122"))
                .get(0).getTipoPagoId()).isEqualTo(11);
        assertThat(parser.parsear("cobv20260120.txt",
                detalle("060020012301101", "20260120", "00000012345", "20260122"))
                .get(0).getTipoPagoId()).isEqualTo(12);
    }

    @Test
    void ignoraCabeceraTrailerYLineasCortas() {
        String contenido = "0" + " ".repeat(100) + "\n"
                + "5corta\n"
                + "9" + " ".repeat(100) + "\n";

        assertThat(parser.parsear("cobc.txt", contenido.getBytes(StandardCharsets.ISO_8859_1))).isEmpty();
    }

    private byte[] detalle(String idFactura, String fechaAplicacion, String importe, String fechaAcreditacion) {
        String linea = "5"
                + " ".repeat(19)
                + String.format("%-20s", idFactura)
                + "20260115"
                + "0"
                + fechaAplicacion
                + importe
                + "0"
                + fechaAcreditacion
                + " ".repeat(20);
        return linea.getBytes(StandardCharsets.ISO_8859_1);
    }

}
