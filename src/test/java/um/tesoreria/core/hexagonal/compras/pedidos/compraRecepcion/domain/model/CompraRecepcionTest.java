package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompraRecepcionTest {

    @Test
    void resultadoMasGravePriorizaLoNoConforme() {
        assertThat(CompraRecepcionResultado.masGrave(List.of(CompraRecepcionResultado.CONFORME,
                CompraRecepcionResultado.CON_OBSERVACIONES))).isEqualTo(CompraRecepcionResultado.CON_OBSERVACIONES);
        assertThat(CompraRecepcionResultado.masGrave(List.of(CompraRecepcionResultado.CONFORME,
                CompraRecepcionResultado.NO_CONFORME, CompraRecepcionResultado.CON_OBSERVACIONES)))
                .isEqualTo(CompraRecepcionResultado.NO_CONFORME);
        assertThat(CompraRecepcionResultado.masGrave(List.of())).isEqualTo(CompraRecepcionResultado.CONFORME);
        assertThat(CompraRecepcionResultado.masGrave(null)).isEqualTo(CompraRecepcionResultado.CONFORME);
        assertThat(CompraRecepcionResultado.masGrave(java.util.Arrays.asList(
                CompraRecepcionResultado.CONFORME, null)))
                .isEqualTo(CompraRecepcionResultado.CONFORME);
    }

    @Test
    void soloLoConformeCubreElSaldoContratado() {
        assertThat(CompraRecepcionResultado.CONFORME.cubreSaldo()).isTrue();
        assertThat(CompraRecepcionResultado.CON_OBSERVACIONES.cubreSaldo()).isTrue();
        assertThat(CompraRecepcionResultado.NO_CONFORME.cubreSaldo()).isFalse();
    }

    @Test
    void resultadoGeneralSeDerivaDelDetalle() {
        CompraRecepcion recepcion = CompraRecepcion.builder()
                .items(List.of(
                        CompraRecepcionItem.builder().resultado(CompraRecepcionResultado.CONFORME).build(),
                        CompraRecepcionItem.builder().resultado(CompraRecepcionResultado.NO_CONFORME).build()))
                .build();

        assertThat(recepcion.resultadoGeneral()).isEqualTo(CompraRecepcionResultado.NO_CONFORME);
        assertThat(CompraRecepcion.builder().build().resultadoGeneral())
                .isEqualTo(CompraRecepcionResultado.CONFORME);
    }

    @Test
    void pendienteCompletoSoloConTodosLosItemsCubiertos() {
        CompraRecepcionPendienteItem cubierto = new CompraRecepcionPendienteItem(100L, 1, 1,
                new BigDecimal("3.00"), new BigDecimal("3.00"), BigDecimal.ZERO, "Unidad", "Notebook",
                null, null, null, null);
        CompraRecepcionPendienteItem pendiente = new CompraRecepcionPendienteItem(101L, 2, 2,
                new BigDecimal("2.00"), BigDecimal.ZERO, new BigDecimal("2.00"), "Unidad", "Mouse",
                null, null, null, null);

        assertThat(cubierto.completo()).isTrue();
        assertThat(pendiente.completo()).isFalse();
        assertThat(new CompraRecepcionPendiente(50L, "OC-1", null, 15, 7, null, null, null,
                List.of(cubierto), List.of()).completo()).isTrue();
        assertThat(new CompraRecepcionPendiente(50L, "OC-1", null, 15, 7, null, null, null,
                List.of(cubierto, pendiente), List.of()).completo()).isFalse();
        assertThat(new CompraRecepcionPendiente(50L, "OC-1", null, 15, 7, null, null, null,
                List.of(), List.of()).completo()).isFalse();
    }

}
