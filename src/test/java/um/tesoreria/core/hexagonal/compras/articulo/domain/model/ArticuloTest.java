package um.tesoreria.core.hexagonal.compras.articulo.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ArticuloTest {

    @Test
    void conCambiosVacios_esIgualAlActual() {
        var actual = completo();

        var resultado = actual.conCambios(new Articulo());

        assertThat(resultado).usingRecursiveComparison().ignoringFields("cuenta").isEqualTo(actual);
    }

    @Test
    void conCambios_aplicaCadaCampoNoNulo_yConservaElId() {
        var cambios = Articulo.builder().articuloId(99L).nombre("N2").descripcion("D2").unidad("U2").precio(BigDecimal.ONE)
                .inventariable((byte) 1).stockMinimo(5L).numeroCuenta(new BigDecimal("2")).tipo("bien")
                .directo((byte) 0).habilitado((byte) 0).build();

        var resultado = completo().conCambios(cambios);

        assertThat(resultado).usingRecursiveComparison().ignoringFields("articuloId", "cuenta").isEqualTo(cambios);
        assertThat(resultado.getArticuloId()).isEqualTo(1L);
        assertThat(resultado.getCuenta()).isNull();
    }

    private static Articulo completo() {
        return Articulo.builder().articuloId(1L).nombre("N").descripcion("D").unidad("U").precio(BigDecimal.TEN)
                .inventariable((byte) 0).stockMinimo(0L).numeroCuenta(new BigDecimal("1")).tipo("gasto")
                .directo((byte) 1).habilitado((byte) 1).build();
    }
}
