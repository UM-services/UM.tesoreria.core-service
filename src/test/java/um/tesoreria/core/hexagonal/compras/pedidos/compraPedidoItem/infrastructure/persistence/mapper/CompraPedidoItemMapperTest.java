package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.model.CompraPedidoItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.persistence.entity.CompraPedidoItemEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CompraPedidoItemMapperTest {

    private final CompraPedidoItemMapper mapper = new CompraPedidoItemMapper();

    @Test
    void roundTripPreservaElDetalle() {
        CompraPedidoItem domain = CompraPedidoItem.builder()
                .compraPedidoItemId(1)
                .compraPedidoId(5)
                .orden(1)
                .cantidad(new BigDecimal("5.00"))
                .unidad("Unidad")
                .descripcion("Notebook")
                .especificaciones("16 GB RAM, SSD 512 GB")
                .referenciaWeb("https://ejemplo.com/notebook")
                .build();

        CompraPedidoItemEntity entity = mapper.toEntity(domain);
        CompraPedidoItem back = mapper.toDomain(entity);

        assertThat(entity.getCompraPedidoId()).isEqualTo(5);
        assertThat(back.getOrden()).isEqualTo(1);
        assertThat(back.getCantidad()).isEqualByComparingTo("5.00");
        assertThat(back.getUnidad()).isEqualTo("Unidad");
        assertThat(back.getDescripcion()).isEqualTo("Notebook");
        assertThat(back.getEspecificaciones()).isEqualTo("16 GB RAM, SSD 512 GB");
        assertThat(back.getReferenciaWeb()).isEqualTo("https://ejemplo.com/notebook");
    }

    @Test
    void nullDevuelveNull() {
        assertThat(mapper.toEntity(null)).isNull();
        assertThat(mapper.toDomain(null)).isNull();
    }

}
