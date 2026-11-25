package um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TopeMovimientosTest {

    @ParameterizedTest
    @CsvSource({"0,400", "-1,400", "2147483647,400", "10,0", "10,-5"})
    void rechazaTopesQueHarianFallarTodosLosPedidos(int lineas, int dias) {
        assertThatThrownBy(() -> new TopeMovimientos(lineas, dias)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void aceptaLosBordesValidos() {
        TopeMovimientos minimos = new TopeMovimientos(1, 1);
        TopeMovimientos maximos = new TopeMovimientos(Integer.MAX_VALUE - 1, Integer.MAX_VALUE);

        assertThat(minimos.maximo()).isEqualTo(1);
        assertThat(minimos.maximoDias()).isEqualTo(1);
        assertThat(maximos.maximo()).isEqualTo(Integer.MAX_VALUE - 1);
        assertThat(maximos.maximoDias()).isEqualTo(Integer.MAX_VALUE);
    }
}
