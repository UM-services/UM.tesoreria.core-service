package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.service;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ResolutorMinimoCotizacionesTest {

    private static final BigDecimal REFERENCIA = new BigDecimal("100000");

    private static CompraCotizacionTramo tramo(Integer tope, Integer cantidad, int activo) {
        return CompraCotizacionTramo.builder()
                .topeMultiplico(tope)
                .cantidadMinima(cantidad)
                .activo((byte) activo)
                .build();
    }

    private final List<CompraCotizacionTramo> tramos = List.of(
            tramo(2, 1, 1),
            tramo(6, 2, 1),
            tramo(null, 3, 1));

    @Test
    void resuelveElTramoQueAlcanzaElMonto() {
        assertThat(ResolutorMinimoCotizaciones.resolver(tramos, REFERENCIA, new BigDecimal("150000")))
                .contains(1);
        assertThat(ResolutorMinimoCotizaciones.resolver(tramos, REFERENCIA, new BigDecimal("200000")))
                .contains(1);
        assertThat(ResolutorMinimoCotizaciones.resolver(tramos, REFERENCIA, new BigDecimal("500000")))
                .contains(2);
    }

    @Test
    void elTramoAbiertoCubreElResto() {
        assertThat(ResolutorMinimoCotizaciones.resolver(tramos, REFERENCIA, new BigDecimal("900000")))
                .contains(3);
    }

    @Test
    void ignoraLosTramosInactivos() {
        List<CompraCotizacionTramo> conInactivo = List.of(
                tramo(2, 1, 0),
                tramo(6, 2, 1),
                tramo(null, 3, 1));

        assertThat(ResolutorMinimoCotizaciones.resolver(conInactivo, REFERENCIA, new BigDecimal("150000")))
                .contains(2);
    }

    @Test
    void sinReferenciaMontoOTramosDevuelveVacio() {
        assertThat(ResolutorMinimoCotizaciones.resolver(tramos, null, new BigDecimal("150000"))).isEmpty();
        assertThat(ResolutorMinimoCotizaciones.resolver(tramos, REFERENCIA, null)).isEmpty();
        assertThat(ResolutorMinimoCotizaciones.resolver(List.of(), REFERENCIA, new BigDecimal("150000"))).isEmpty();
        assertThat(ResolutorMinimoCotizaciones.resolver(null, REFERENCIA, new BigDecimal("150000"))).isEmpty();
    }

    @Test
    void sinTramoAbiertoNiTopeSuficienteDevuelveVacio() {
        List<CompraCotizacionTramo> sinAbierto = List.of(
                tramo(2, 1, 1),
                tramo(6, 2, 1));

        assertThat(ResolutorMinimoCotizaciones.resolver(sinAbierto, REFERENCIA, new BigDecimal("900000")))
                .isEqualTo(Optional.empty());
    }
}
