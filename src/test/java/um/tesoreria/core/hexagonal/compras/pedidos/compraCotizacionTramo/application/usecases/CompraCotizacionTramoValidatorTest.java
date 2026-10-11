package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.usecases;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.exception.CompraCotizacionTramoException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CompraCotizacionTramoValidatorTest {

    private CompraCotizacionTramo tramo(Integer tope, Integer cantidad) {
        return CompraCotizacionTramo.builder().topeMultiplico(tope).cantidadMinima(cantidad).build();
    }

    @Test
    void validaCamposAceptaValidos() {
        assertThatCode(() -> CompraCotizacionTramoValidator.validarCampos(tramo(2, 1))).doesNotThrowAnyException();
        assertThatCode(() -> CompraCotizacionTramoValidator.validarCampos(tramo(null, 3))).doesNotThrowAnyException();
    }

    @Test
    void validaCamposRechazaNulo() {
        assertThatThrownBy(() -> CompraCotizacionTramoValidator.validarCampos(null))
                .isInstanceOf(CompraCotizacionTramoException.class);
    }

    @Test
    void validaCamposRechazaTopeMenorAUno() {
        assertThatThrownBy(() -> CompraCotizacionTramoValidator.validarCampos(tramo(0, 1)))
                .isInstanceOf(CompraCotizacionTramoException.class)
                .hasMessageContaining("tope");
    }

    @Test
    void validaCamposRechazaCantidadFueraDeRango() {
        assertThatThrownBy(() -> CompraCotizacionTramoValidator.validarCampos(tramo(2, 0)))
                .isInstanceOf(CompraCotizacionTramoException.class);
        assertThatThrownBy(() -> CompraCotizacionTramoValidator.validarCampos(tramo(2, 11)))
                .isInstanceOf(CompraCotizacionTramoException.class);
    }

    @Test
    void validarConjuntoRechazaVacio() {
        assertThatThrownBy(() -> CompraCotizacionTramoValidator.validarConjunto(List.of()))
                .isInstanceOf(CompraCotizacionTramoException.class);
        assertThatThrownBy(() -> CompraCotizacionTramoValidator.validarConjunto(null))
                .isInstanceOf(CompraCotizacionTramoException.class);
    }

    @Test
    void validarConjuntoRechazaCantidadNula() {
        assertThatThrownBy(() -> CompraCotizacionTramoValidator.validarConjunto(List.of(tramo(2, null))))
                .isInstanceOf(CompraCotizacionTramoException.class)
                .hasMessageContaining("cantidad mínima");
    }

    @Test
    void validarConjuntoRechazaTopeDuplicado() {
        List<CompraCotizacionTramo> tramos = new ArrayList<>(List.of(tramo(2, 1)));
        tramos.add(tramo(2, 2));

        assertThatThrownBy(() -> CompraCotizacionTramoValidator.validarConjunto(tramos))
                .isInstanceOf(CompraCotizacionTramoException.class)
                .hasMessageContaining("tope 2");
    }

    @Test
    void validarConjuntoRechazaDosAbiertos() {
        List<CompraCotizacionTramo> tramos = new ArrayList<>(List.of(tramo(null, 1)));
        tramos.add(tramo(null, 3));

        assertThatThrownBy(() -> CompraCotizacionTramoValidator.validarConjunto(tramos))
                .isInstanceOf(CompraCotizacionTramoException.class)
                .hasMessageContaining("sin tope");
    }

    @Test
    void validarConjuntoAceptaConfiguracionDelPrototipo() {
        assertThatCode(() -> CompraCotizacionTramoValidator.validarConjunto(List.of(tramo(2, 1), tramo(6, 2), tramo(null, 3))))
                .doesNotThrowAnyException();
        assertThat(CompraCotizacionTramoValidator.CANTIDAD_MINIMA_CEILING).isPositive();
    }
}
