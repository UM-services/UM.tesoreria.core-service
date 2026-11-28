package um.tesoreria.core.service;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.kotlin.model.ChequeraImpresionCabecera;
import um.tesoreria.core.kotlin.model.ChequeraImpresionDetalle;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChequeraImpresionSnapshotComparatorTest {

    private static final OffsetDateTime FECHA = OffsetDateTime.of(2026, 11, 27, 10, 0, 0, 0, ZoneOffset.UTC);

    @Test
    void dosSnapshotsConElMismoContenidoSonIgualesAunqueCambienIdsYFecha() {
        var cabecera = cabecera(1L, FECHA);
        var otraCabecera = cabecera(2L, FECHA.plusMinutes(5));

        assertThat(ChequeraImpresionSnapshotComparator.sameSnapshot(
                cabecera, detalles(1L), otraCabecera, detalles(2L))).isTrue();
    }

    @Test
    void unaEscalaDistintaDeImporteNoCuentaComoCambio() {
        var detalle = detalle(1L, new BigDecimal("211500"));
        var otro = detalle(2L, new BigDecimal("211500.00"));

        assertThat(ChequeraImpresionSnapshotComparator.sameSnapshot(
                cabecera(1L, FECHA), List.of(detalle),
                cabecera(2L, FECHA), List.of(otro))).isTrue();
    }

    @Test
    void unImporteDistintoSiEsUnCambio() {
        var detalle = detalle(1L, new BigDecimal("211500"));
        var otro = detalle(2L, new BigDecimal("211501"));

        assertThat(ChequeraImpresionSnapshotComparator.sameSnapshot(
                cabecera(1L, FECHA), List.of(detalle),
                cabecera(2L, FECHA), List.of(otro))).isFalse();
    }

    @Test
    void distintaCantidadDeDetallesEsUnCambio() {
        assertThat(ChequeraImpresionSnapshotComparator.sameSnapshot(
                cabecera(1L, FECHA), detalles(1L),
                cabecera(2L, FECHA), List.of(detalle(2L, new BigDecimal("211500"))))).isFalse();
    }

    private ChequeraImpresionCabecera cabecera(Long id, OffsetDateTime fecha) {
        return new ChequeraImpresionCabecera(id, 4, 2, 28988L, fecha,
                new BigDecimal("47196087"), 1, 11, 2, 46, 1, "earabia", 433L,
                (byte) 0, new BigDecimal("0"), null, null, null);
    }

    private List<ChequeraImpresionDetalle> detalles(Long baseId) {
        return List.of(detalle(baseId, new BigDecimal("211500")),
                detalle(baseId + 100, new BigDecimal("100000")));
    }

    private ChequeraImpresionDetalle detalle(Long id, BigDecimal importe) {
        return new ChequeraImpresionDetalle(id, 1L, 4, 2, 28988L, 3, 1, 1,
                "Arancel Mensual", new BigDecimal("2326500"), 12, 2, 2027, 46,
                OffsetDateTime.of(2027, 2, 10, 0, 0, 0, 0, ZoneOffset.UTC), importe,
                OffsetDateTime.of(2027, 2, 28, 0, 0, 0, 0, ZoneOffset.UTC), new BigDecimal("214755"),
                OffsetDateTime.of(2027, 3, 31, 0, 0, 0, 0, ZoneOffset.UTC), new BigDecimal("221010"),
                "earabia", 433L);
    }

}
