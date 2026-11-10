package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.parser;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.LineaImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.RendicionPayPerTic;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.PayPerTicRendicionRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PayPerTicPeriodoParserTest {

    @Test
    void traduceLasFilasPendientesDelPeriodoASusClaves() {
        PayPerTicPeriodoParser parser = new PayPerTicPeriodoParser(rendiciones(List.of(
                new RendicionPayPerTic("ppt-1", "0600200123100001", new BigDecimal("123.45"),
                        OffsetDateTime.parse("2026-03-15T00:00:00Z"),
                        OffsetDateTime.parse("2026-03-17T00:00:00Z"),
                        OffsetDateTime.parse("2026-04-01T00:00:00Z")))));

        List<LineaImportacion> lineas = parser.porPeriodo(
                OffsetDateTime.parse("2026-03-01T00:00:00Z"),
                OffsetDateTime.parse("2026-03-31T23:59:59Z"));

        assertThat(lineas).hasSize(1);
        LineaImportacion linea = lineas.get(0);
        assertThat(linea.getPayperticId()).isEqualTo("ppt-1");
        assertThat(linea.getFacultadId()).isEqualTo(6);
        assertThat(linea.getTipoChequeraId()).isEqualTo(2);
        assertThat(linea.getChequeraSerieId()).isEqualTo(123L);
        assertThat(linea.getProductoId()).isEqualTo(1);
        assertThat(linea.getCuotaId()).isEqualTo(1);
        assertThat(linea.getImporte()).isEqualByComparingTo(new BigDecimal("123.45"));
        assertThat(linea.getFechaPago()).isEqualTo(OffsetDateTime.parse("2026-03-15T00:00:00Z"));
        assertThat(linea.getTipoPagoId()).isEqualTo(8);
    }

    @Test
    void noSeUsaComoParserDeArchivo() {
        PayPerTicPeriodoParser parser = new PayPerTicPeriodoParser(rendiciones(List.of()));

        assertThat(parser.soportaPeriodo()).isTrue();
        assertThatThrownBy(() -> parser.parsear("paypertic.xlsx", new byte[]{1}))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    private PayPerTicRendicionRepository rendiciones(List<RendicionPayPerTic> filas) {
        return new PayPerTicRendicionRepository() {
            @Override
            public List<RendicionPayPerTic> findPendientesByPeriodo(OffsetDateTime desde, OffsetDateTime hasta) {
                return filas;
            }

            @Override
            public void marcarImportado(String payperticId) {
                // sin efecto en el test
            }
        };
    }

}
