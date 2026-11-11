package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.layout;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.DebitoPendiente;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DebitoDirectoLayoutTest {

    private static final OffsetDateTime FECHA = OffsetDateTime.parse("2026-03-15T00:00:00Z");
    private static final String HORA = "143005";

    private final DebitoPendiente debito = DebitoPendiente.builder()
            .debitoId(12345L)
            .facultadId(6)
            .tipoChequeraId(2)
            .chequeraSerieId(123L)
            .productoId(1)
            .alternativaId(1)
            .cuotaId(1)
            .cbu("0720000012345678901234")
            .fechaVencimiento(OffsetDateTime.parse("2026-03-31T00:00:00Z"))
            .build();

    @Test
    void generaUnDetalleDeSeiscientasPosiciones() {
        String detalle = DebitoDirectoLayout.detalle(debito, new BigDecimal("123.45"),
                "12345678", "45719365", FECHA, HORA);

        assertThat(detalle).hasSize(600);
        assertThat(detalle.substring(0, 11)).isEqualTo("30518594466");
        assertThat(detalle.substring(11, 21)).isEqualTo("DEBITO    ");
        assertThat(detalle.substring(21, 23)).isEqualTo("37");
        assertThat(detalle.substring(35, 43)).isEqualTo("00720000");
        assertThat(detalle.substring(43, 60)).isEqualTo("00012345678901234");
        assertThat(detalle.substring(60, 82)).isEqualTo("1234567845719365      ");
        assertThat(detalle.substring(97, 98)).isEqualTo("0");
        assertThat(detalle.substring(98, 106)).isEqualTo("20260331");
        assertThat(detalle.substring(151, 167)).isEqualTo("0000000000012345");
        assertThat(detalle.substring(167, 182)).isEqualTo("12345          ");
        assertThat(detalle.substring(405, 413)).isEqualTo("20260315");
        assertThat(detalle.substring(413, 417)).isEqualTo("1430");
        assertThat(detalle.substring(479, 495)).isEqualTo("UNIV DE MENDOZA ");
        assertThat(detalle.substring(578, 586)).isEqualTo("20260315");
        assertThat(detalle.substring(586, 592)).isEqualTo("143005");
    }

    @Test
    void generaElRegistroDeControlConLaCantidadYElTotalEnCentavos() {
        String control = DebitoDirectoLayout.control(FECHA, HORA, 2, 24690L);

        assertThat(control).hasSize(600);
        assertThat(control.substring(0, 21)).isEqualTo("9".repeat(21));
        assertThat(control.substring(28, 36)).isEqualTo("20260315");
        assertThat(control.substring(36, 42)).isEqualTo("143005");
        assertThat(control.substring(101, 110)).isEqualTo("000000002");
        assertThat(control.substring(110, 128)).isEqualTo("000000000000024690");
        assertThat(control.substring(425, 434)).isEqualTo("000000003");
    }

    @Test
    void completaElCbuA22DigitosYArmaElNombreDelArchivo() {
        assertThat(DebitoDirectoLayout.normalizarCbu("721234567890123456789"))
                .isEqualTo("0721234567890123456789");
        assertThat(DebitoDirectoLayout.filename(true, false, FECHA)).isEqualTo("DebitoAut.Santander.150326");
        assertThat(DebitoDirectoLayout.filename(false, true, FECHA)).isEqualTo("DebitoAut.OtrosBancos.150326");
    }

}
