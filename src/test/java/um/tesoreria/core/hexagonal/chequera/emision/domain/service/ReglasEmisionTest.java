package um.tesoreria.core.hexagonal.chequera.emision.domain.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReglasEmisionTest {

    @Test
    void redondeaHaciaArribaConDosDecimales() {
        assertEquals(new BigDecimal("33.34"), ReglasEmision.redondear2(new BigDecimal("33.335")));
        assertEquals(new BigDecimal("0.00"), ReglasEmision.redondear2(null));
    }

    @Test
    void aplicaBecaYArancel() {
        // 1000 * (1 - 0.20) * 100 / 100 = 800.00
        assertEquals(new BigDecimal("800.00"), ReglasEmision.aplicarBecaYArancel(
                new BigDecimal("1000.00"), new BigDecimal("0.20"), new BigDecimal("100")));
        // 1000 * 1 * 50 / 100 = 500.00
        assertEquals(new BigDecimal("500.00"), ReglasEmision.aplicarBecaYArancel(
                new BigDecimal("1000.00"), BigDecimal.ZERO, new BigDecimal("50")));
    }

    @Test
    void productoMatriculaNoAplicaBeca() {
        assertEquals(BigDecimal.ZERO,
                ReglasEmision.becaAplicable(new BigDecimal("0.30"), ReglasEmision.PRODUCTO_SIN_BECA_ID));
        assertEquals(new BigDecimal("0.30"), ReglasEmision.becaAplicable(new BigDecimal("0.30"), 1));
    }

    @Test
    void descuentoPagoTermino() {
        assertEquals(0, new BigDecimal("0.90").compareTo(
                ReglasEmision.multiplicadorDescuentoPagoTermino(10)));
        assertEquals(0, BigDecimal.ONE.compareTo(
                ReglasEmision.multiplicadorDescuentoPagoTermino(0)));
    }

    @Test
    void corteTemporalDeArancel() {
        assertEquals(202108, ReglasEmision.periodo(2021, 8));
        assertFalse(ReglasEmision.periodo(2021, 8) > ReglasEmision.CORTE_ARANCEL_POR_CUOTA);
        assertTrue(ReglasEmision.periodo(2021, 9) > ReglasEmision.CORTE_ARANCEL_POR_CUOTA);
    }

    @Test
    void validaCoherenciaDeCuota() {
        var desde = java.time.OffsetDateTime.parse("2026-03-01T00:00:00Z");
        var hasta = java.time.OffsetDateTime.parse("2026-03-20T00:00:00Z");
        assertTrue(ReglasEmision.cuotaConsistente(new BigDecimal("100"), new BigDecimal("110"),
                new BigDecimal("120"), desde, hasta, hasta.plusDays(10)));
        assertFalse(ReglasEmision.cuotaConsistente(new BigDecimal("120"), new BigDecimal("110"),
                new BigDecimal("120"), desde, hasta, hasta.plusDays(10)));
        assertFalse(ReglasEmision.cuotaConsistente(new BigDecimal("100"), new BigDecimal("110"),
                new BigDecimal("120"), hasta, desde, hasta.plusDays(10)));
    }

    @Test
    void validaEmail() {
        assertTrue(ReglasEmision.emailValido("persona@um.edu.ar"));
        assertFalse(ReglasEmision.emailValido("persona"));
        assertFalse(ReglasEmision.emailValido(null));
    }
}
