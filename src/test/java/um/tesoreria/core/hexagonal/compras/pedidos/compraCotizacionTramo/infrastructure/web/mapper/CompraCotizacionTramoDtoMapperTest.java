package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.web.dto.CompraCotizacionTramoRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.web.dto.CompraCotizacionTramoResponse;

import static org.assertj.core.api.Assertions.assertThat;

class CompraCotizacionTramoDtoMapperTest {

    private final CompraCotizacionTramoDtoMapper mapper = new CompraCotizacionTramoDtoMapper();

    @Test
    void toDomainCopiaCampos() {
        CompraCotizacionTramo domain = mapper.toDomain(CompraCotizacionTramoRequest.builder()
                .nombre("N1").topeMultiplico(2).cantidadMinima(1).activo((byte) 1).build());

        assertThat(domain.getNombre()).isEqualTo("N1");
        assertThat(domain.getTopeMultiplico()).isEqualTo(2);
        assertThat(domain.getCantidadMinima()).isEqualTo(1);
    }

    @Test
    void toResponseMarcaAbiertoCuandoTopeEsNulo() {
        CompraCotizacionTramoResponse conTope = mapper.toResponse(
                CompraCotizacionTramo.builder().nombre("N1").topeMultiplico(2).cantidadMinima(1).build());
        CompraCotizacionTramoResponse abierto = mapper.toResponse(
                CompraCotizacionTramo.builder().nombre("Sin tope").topeMultiplico(null).cantidadMinima(3).build());

        assertThat(conTope.getAbierto()).isFalse();
        assertThat(abierto.getAbierto()).isTrue();
    }

    @Test
    void mapeaNulos() {
        assertThat(mapper.toDomain(null)).isNull();
        assertThat(mapper.toResponse(null)).isNull();
    }

}
