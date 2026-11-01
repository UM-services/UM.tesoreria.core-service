package um.tesoreria.core.hexagonal.contable.cuenta.domain.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class CuentaJerarquiaTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"0", "-11010100000", "9999999999", "100000000000", "11010100000.1",
            "999999999999999999999999", "10001000000", "11000010000", "11010000001", "11000010001"})
    void rechazaNumerosInvalidosEnTodasLasOperaciones(String valor) {
        BigDecimal numero = valor == null ? null : new BigDecimal(valor);

        assertThatIllegalArgumentException().isThrownBy(() -> CuentaJerarquia.validar(numero));
        assertThatIllegalArgumentException().isThrownBy(() -> CuentaJerarquia.grados(numero));
        assertThatIllegalArgumentException().isThrownBy(() -> CuentaJerarquia.padre(numero));
        assertThatIllegalArgumentException().isThrownBy(() -> CuentaJerarquia.ancestros(numero));
        assertThatIllegalArgumentException().isThrownBy(() -> CuentaJerarquia.rangoDescendientes(numero));
    }

    @ParameterizedTest
    @CsvSource({
            "10000000000, 1, 10000000000, 10000000000, 10000000000, 10000000000, 19999999999",
            "11000000000, 2, 10000000000, 11000000000, 11000000000, 11000000000, 11099999999",
            "11010000000, 3, 10000000000, 11000000000, 11010000000, 11010000000, 11010999999",
            "11010100000, 4, 10000000000, 11000000000, 11010000000, 11010100000, 11010109999",
            "11010100001, 5, 10000000000, 11000000000, 11010000000, 11010100000, 11010100001",
            "11010100010, 5, 10000000000, 11000000000, 11010000000, 11010100000, 11010100010",
            "11010010001, 5, 10000000000, 11000000000, 11010000000, 11010010000, 11010010001",
            "99999999999, 5, 90000000000, 99900000000, 99999000000, 99999990000, 99999999999"
    })
    void calculaGradosYRangoPorGrupos(String valor, int grado, String grado1, String grado2,
                                     String grado3, String grado4, String hasta) {
        BigDecimal numero = new BigDecimal(valor);

        assertThat(CuentaJerarquia.grados(numero)).isEqualTo(new CuentaJerarquia.Grados(grado,
                new BigDecimal(grado1), new BigDecimal(grado2), new BigDecimal(grado3), new BigDecimal(grado4)));
        assertThat(CuentaJerarquia.rangoDescendientes(numero))
                .isEqualTo(new CuentaJerarquia.Rango(numero, new BigDecimal(hasta)));
    }

    @ParameterizedTest
    @MethodSource("jerarquias")
    void obtienePadreYAncestrosDeRaizAPadre(String valor, List<BigDecimal> ancestros) {
        BigDecimal numero = new BigDecimal(valor);

        assertThat(CuentaJerarquia.ancestros(numero)).isEqualTo(ancestros);
        if (ancestros.isEmpty()) {
            assertThat(CuentaJerarquia.padre(numero)).isEmpty();
        } else {
            assertThat(CuentaJerarquia.padre(numero)).contains(ancestros.getLast());
        }
    }

    static Stream<Arguments> jerarquias() {
        return Stream.of(
                Arguments.of("10000000000", numeros()),
                Arguments.of("11000000000", numeros("10000000000")),
                Arguments.of("11010000000", numeros("10000000000", "11000000000")),
                Arguments.of("11010100000", numeros("10000000000", "11000000000", "11010000000")),
                Arguments.of("11010100001", numeros("10000000000", "11000000000", "11010000000", "11010100000")));
    }

    @Test
    void cerosDentroDeUnGrupoNoAmplianElRangoAHermanas() {
        var rango = CuentaJerarquia.rangoDescendientes(new BigDecimal("11010100000"));

        assertThat(rango.hasta()).isEqualByComparingTo("11010109999");
        assertThat(new BigDecimal("11010110000")).isGreaterThan(rango.hasta());
        assertThat(CuentaJerarquia.padre(new BigDecimal("11010100000")))
                .contains(new BigDecimal("11010000000"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"11010100000.0000", "1.10101E+10"})
    void aceptaElMismoEnteroIndependientementeDeSuEscala(String valor) {
        BigDecimal numero = new BigDecimal(valor);
        BigDecimal entero = new BigDecimal("11010100000");

        CuentaJerarquia.validar(numero);
        assertThat(CuentaJerarquia.grados(numero)).isEqualTo(CuentaJerarquia.grados(entero));
        assertThat(CuentaJerarquia.padre(numero)).isEqualTo(CuentaJerarquia.padre(entero));
        assertThat(CuentaJerarquia.ancestros(numero)).isEqualTo(CuentaJerarquia.ancestros(entero));
        assertThat(CuentaJerarquia.rangoDescendientes(numero)).isEqualTo(CuentaJerarquia.rangoDescendientes(entero));
    }

    private static List<BigDecimal> numeros(String... valores) {
        return Stream.of(valores).map(BigDecimal::new).toList();
    }
}
