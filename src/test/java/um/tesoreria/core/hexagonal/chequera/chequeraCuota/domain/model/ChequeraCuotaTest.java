package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ChequeraCuotaTest {

    private ChequeraCuota.ChequeraCuotaBuilder cuotaConImportes(String importe1, String importe2, String importe3) {
        return ChequeraCuota.builder()
                .facultadId(2)
                .tipoChequeraId(517)
                .chequeraSerieId(2788L)
                .productoId(3)
                .alternativaId(1)
                .cuotaId(1)
                .arancelTipoId(46)
                .importe1Original(new BigDecimal(importe1))
                .importe2Original(new BigDecimal(importe2))
                .importe3Original(new BigDecimal(importe3));
    }

    @Test
    void aplicaPorcentajeCincuenta() {
        var cuota = cuotaConImportes("325000.0000", "341250.0000", "357500.0000").build();

        cuota.aplicarPorcentajeArancel(new BigDecimal("50.00"));

        assertThat(cuota.getImporte1()).isEqualByComparingTo("162500.0000");
        assertThat(cuota.getImporte2()).isEqualByComparingTo("170625.0000");
        assertThat(cuota.getImporte3()).isEqualByComparingTo("178750.0000");
        // El original no se toca
        assertThat(cuota.getImporte1Original()).isEqualByComparingTo("325000.0000");
        assertThat(cuota.getImporte3Original()).isEqualByComparingTo("357500.0000");
    }

    @Test
    void porcentajeCompletoNoCambiaImportes() {
        var cuota = cuotaConImportes("325000.0000", "341250.0000", "357500.0000").build();

        cuota.aplicarPorcentajeArancel(new BigDecimal("100"));

        assertThat(cuota.getImporte1()).isEqualByComparingTo("325000.0000");
        assertThat(cuota.getImporte3()).isEqualByComparingTo("357500.0000");
    }

    @Test
    void porcentajeCeroDejaImportesEnCero() {
        var cuota = cuotaConImportes("325000.0000", "341250.0000", "357500.0000").build();

        cuota.aplicarPorcentajeArancel(BigDecimal.ZERO);

        assertThat(cuota.getImporte1()).isEqualByComparingTo("0.0000");
        assertThat(cuota.getImporte3()).isEqualByComparingTo("0.0000");
    }

    @Test
    void porcentajeNuloNoModificaImportes() {
        var cuota = cuotaConImportes("325000.0000", "341250.0000", "357500.0000")
                .importe1(new BigDecimal("999.0000")).build();

        cuota.aplicarPorcentajeArancel(null);

        assertThat(cuota.getImporte1()).isEqualByComparingTo("999.0000");
    }

    @Test
    void importeOriginalNuloNoModificaElImporte() {
        var cuota = ChequeraCuota.builder()
                .facultadId(2).tipoChequeraId(517).chequeraSerieId(2788L).productoId(3).alternativaId(1).cuotaId(1)
                .importe1(new BigDecimal("123.0000"))
                .importe2Original(new BigDecimal("341250.0000"))
                .build();

        cuota.aplicarPorcentajeArancel(new BigDecimal("50"));

        assertThat(cuota.getImporte1()).isEqualByComparingTo("123.0000"); // sin original, intacto
        assertThat(cuota.getImporte2()).isEqualByComparingTo("170625.0000");
    }

    @Test
    void esIdempotente() {
        var cuota = cuotaConImportes("325000.0000", "341250.0000", "357500.0000").build();

        cuota.aplicarPorcentajeArancel(new BigDecimal("50"));
        var primeraVez = cuota.getImporte1();
        cuota.aplicarPorcentajeArancel(new BigDecimal("50"));

        assertThat(cuota.getImporte1()).isEqualByComparingTo(primeraVez).isEqualByComparingTo("162500.0000");
    }

    @Test
    void redondeaAEscalaCuatroHalfUp() {
        // 1 * 33.33 / 100 = 0.3333
        var cuota = cuotaConImportes("1.0000", "2.0000", "3.0000").build();

        cuota.aplicarPorcentajeArancel(new BigDecimal("33.33"));

        assertThat(cuota.getImporte1()).isEqualByComparingTo("0.3333");
        assertThat(cuota.getImporte1().scale()).isEqualTo(4);
    }

}
