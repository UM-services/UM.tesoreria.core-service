package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.layout;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.CuotaDeuda;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FacturacionLayoutTest {

    private static final OffsetDateTime FECHA = OffsetDateTime.parse("2026-03-15T00:00:00Z");

    private final CuotaDeuda cuota = CuotaDeuda.builder()
            .facultadId(6)
            .tipoChequeraId(2)
            .chequeraSerieId(123L)
            .productoId(1)
            .alternativaId(1)
            .cuotaId(1)
            .mes(3)
            .anho(2026)
            .vencimiento1(FECHA)
            .importe1(new BigDecimal("123.45"))
            .vencimiento2(FECHA.plusDays(10))
            .importe2(new BigDecimal("150.00"))
            .vencimiento3(FECHA.plusDays(20))
            .importe3(new BigDecimal("180.00"))
            .codigoBarras("2550600200123101001234503152026")
            .build();

    @Test
    void generaElHeaderYTrailerDePmcConEmpresaDeCuatroPosiciones() {
        String header = PmcLayout.header(FECHA);
        String trailer = PmcLayout.trailer(FECHA, 1, new BigDecimal("123.45"));

        assertThat(header).hasSize(280).startsWith("04001417" + "20260315");
        assertThat(trailer).hasSize(280).startsWith("94001417" + "20260315");
        assertThat(trailer.substring(16, 23)).isEqualTo("0000001");
        assertThat(trailer.substring(30, 41)).isEqualTo("00000012345");
    }

    @Test
    void generaElHeaderYTrailerDeClickDePagosConEmpresaDeSeisPosiciones() {
        String header = ClickPagosLayout.header(FECHA);
        String trailer = ClickPagosLayout.trailer(FECHA, 1, new BigDecimal("123.45"));

        assertThat(header).hasSize(282).startsWith("0400791893" + "20260315");
        assertThat(trailer).hasSize(282).startsWith("9400791893" + "20260315");
        assertThat(trailer.substring(18, 25)).isEqualTo("0000001");
        assertThat(trailer.substring(32, 48)).isEqualTo("0000000000012345");
    }

    @Test
    void generaElDetalleConLasPosicionesHistoricas() {
        String detalle = PmcLayout.detalle(cuota);

        assertThat(detalle).hasSize(280);
        assertThat(detalle.charAt(0)).isEqualTo('5');
        assertThat(detalle.substring(1, 20)).isEqualTo("0600200123         ");
        assertThat(detalle.substring(20, 40)).isEqualTo("060020012301101     ");
        assertThat(detalle.substring(40, 41)).isEqualTo("0");
        assertThat(detalle.substring(41, 49)).isEqualTo("20260315");
        assertThat(detalle.substring(49, 60)).isEqualTo("00000012345");
        assertThat(detalle.substring(60, 68)).isEqualTo("20260325");
        assertThat(detalle.substring(68, 79)).isEqualTo("00000015000");
        assertThat(detalle.substring(79, 87)).isEqualTo("20260404");
        assertThat(detalle.substring(87, 98)).isEqualTo("00000018000");
        String barcode = cuota.getCodigoBarras();
        assertThat(detalle.substring(191, 251)).isEqualTo(barcode + " ".repeat(60 - barcode.length()));
        assertThat(detalle.substring(251)).isEqualTo("0".repeat(29));
    }

    @Test
    void generarDosVecesConLaMismaFechaProduceElMismoDetalle() {
        assertThat(PmcLayout.detalle(cuota)).isEqualTo(PmcLayout.detalle(cuota));
        assertThat(ClickPagosLayout.detalle(cuota)).isEqualTo(PmcLayout.detalle(cuota));
    }

}
