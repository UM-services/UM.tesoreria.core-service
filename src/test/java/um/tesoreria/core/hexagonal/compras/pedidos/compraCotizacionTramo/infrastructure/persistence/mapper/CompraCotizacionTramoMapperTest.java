package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.persistence.entity.CompraCotizacionTramoEntity;

import static org.assertj.core.api.Assertions.assertThat;

class CompraCotizacionTramoMapperTest {

    private final CompraCotizacionTramoMapper mapper = new CompraCotizacionTramoMapper();

    @Test
    void toDomainConservaTopeNuloComoAbierto() {
        CompraCotizacionTramo domain = mapper.toDomain(CompraCotizacionTramoEntity.builder()
                .cotizacionTramoId(1L).nombre("Sin tope").topeMultiplico(null).cantidadMinima(3).build());

        assertThat(domain.getTopeMultiplico()).isNull();
        assertThat(domain.esAbierto()).isTrue();
        assertThat(domain.getActivo()).isEqualTo((byte) 1);
    }

    @Test
    void toEntityYDomainSonSimetricos() {
        CompraCotizacionTramo domain = CompraCotizacionTramo.builder()
                .cotizacionTramoId(1L).nombre("N1").topeMultiplico(2).cantidadMinima(1).activo((byte) 0).build();

        CompraCotizacionTramoEntity entity = mapper.toEntity(domain);

        assertThat(entity.getNombre()).isEqualTo("N1");
        assertThat(entity.getTopeMultiplico()).isEqualTo(2);
        assertThat(entity.getCantidadMinima()).isEqualTo(1);
        assertThat(entity.getActivo()).isEqualTo((byte) 0);
    }

    @Test
    void updateEntitySeteaTopeAunqueSeaNulo() {
        CompraCotizacionTramoEntity entity = CompraCotizacionTramoEntity.builder()
                .cotizacionTramoId(1L).nombre("N1").topeMultiplico(2).cantidadMinima(1).build();

        mapper.updateEntity(CompraCotizacionTramo.builder().nombre("N1").topeMultiplico(null).cantidadMinima(3).build(), entity);

        assertThat(entity.getTopeMultiplico()).isNull();
        assertThat(entity.getCantidadMinima()).isEqualTo(3);
    }

    @Test
    void updateEntityIgnoraCantidadNula() {
        CompraCotizacionTramoEntity entity = CompraCotizacionTramoEntity.builder()
                .cotizacionTramoId(1L).topeMultiplico(2).cantidadMinima(1).build();

        mapper.updateEntity(CompraCotizacionTramo.builder().nombre("N1").topeMultiplico(6).build(), entity);

        assertThat(entity.getTopeMultiplico()).isEqualTo(6);
        assertThat(entity.getCantidadMinima()).isEqualTo(1);
    }

    @Test
    void mapeaNulos() {
        assertThat(mapper.toDomain(null)).isNull();
        assertThat(mapper.toEntity(null)).isNull();
        mapper.updateEntity(null, null);
    }

}
