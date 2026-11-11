package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.layout;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.DebitoPendiente;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DebitoVisaLayoutTest {

    private static final OffsetDateTime FECHA = OffsetDateTime.parse("2026-03-15T00:00:00Z");
    private static final String HORA = "1430";

    private final DebitoPendiente debito = DebitoPendiente.builder()
            .debitoId(55L)
            .facultadId(6)
            .tipoChequeraId(2)
            .chequeraSerieId(123L)
            .productoId(1)
            .alternativaId(1)
            .cuotaId(1)
            .numeroTarjeta("4500123412341234")
            .fechaVencimiento(OffsetDateTime.parse("2026-03-31T00:00:00Z"))
            .build();

    @Test
    void generaElHeaderDeCienPosiciones() {
        String header = DebitoVisaLayout.header(FECHA, HORA);

        assertThat(header).hasSize(100);
        assertThat(header).startsWith("0DEBLIQC 0024054728");
        assertThat(header.substring(29, 37)).isEqualTo("20260315");
        assertThat(header.substring(37, 41)).isEqualTo("1430");
        assertThat(header.charAt(41)).isEqualTo('0');
        assertThat(header.charAt(99)).isEqualTo('*');
    }

    @Test
    void generaElDetalleConImporteEnteroYPrimerEnvio() {
        String detalle = DebitoVisaLayout.detalle(debito, new BigDecimal("123.45"), 1,
                BigDecimal.ZERO, true);

        assertThat(detalle).hasSize(100);
        assertThat(detalle.charAt(0)).isEqualTo('1');
        assertThat(detalle.substring(1, 17)).isEqualTo("4500123412341234");
        assertThat(detalle.substring(20, 28)).isEqualTo("00010101");
        assertThat(detalle.substring(28, 36)).isEqualTo("20260331");
        assertThat(detalle.substring(36, 40)).isEqualTo("0005");
        assertThat(detalle.substring(40, 55)).isEqualTo("000000000012300");
        assertThat(detalle.substring(55, 70)).isEqualTo("000000600200123");
        assertThat(detalle.charAt(70)).isEqualTo('E');
        assertThat(detalle.charAt(99)).isEqualTo('*');
    }

    @Test
    void aplicaDescuentoSoloAClaseChequeraDos() {
        String conDescuento = DebitoVisaLayout.detalle(debito, new BigDecimal("123.45"), 2,
                new BigDecimal("10"), false);

        assertThat(conDescuento.substring(40, 55)).isEqualTo("000000000011100");
        assertThat(conDescuento.charAt(70)).isEqualTo(' ');
        assertThat(DebitoVisaLayout.importeConDescuento(new BigDecimal("123.45"), 1,
                new BigDecimal("10"))).isEqualTo(123);
    }

    @Test
    void generaElPieConLaCantidadYLaSumaDeImportes() {
        String pie = DebitoVisaLayout.trailer(FECHA, HORA, 2, 246L);

        assertThat(pie).hasSize(100);
        assertThat(pie).startsWith("9DEBLIQC 0024054728");
        assertThat(pie.substring(41, 48)).isEqualTo("0000002");
        assertThat(pie.substring(48, 63)).isEqualTo("000000000024600");
        assertThat(pie.charAt(99)).isEqualTo('*');
    }

}
