package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.entity.CompraFormalizacionEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.entity.CompraFormalizacionItemEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CompraFormalizacionMapperTest {

    private final CompraFormalizacionMapper mapper = new CompraFormalizacionMapper();

    private CompraFormalizacion domain() {
        return CompraFormalizacion.builder()
                .compraFormalizacionId(1L)
                .compraAdjudicacionId(9L)
                .numero("OC-2026-000001")
                .numeroExterno("OC-EXT")
                .ejercicioId(15)
                .fecha(LocalDateTime.of(2026, 10, 12, 10, 0))
                .proveedorId(7)
                .moneda("ARS")
                .monto(new BigDecimal("28000.00"))
                .condicionPago("30 días")
                .plazoEntrega("48 horas")
                .garantia("12 meses")
                .objeto("<p>Objeto</p>")
                .observacion("obs")
                .usuarioId(38)
                .build();
    }

    @Test
    void cabeceraIdaYVuelta() {
        CompraFormalizacionEntity entity = mapper.toEntity(domain());
        assertThat(entity.getNumero()).isEqualTo("OC-2026-000001");
        assertThat(entity.getCompraAdjudicacionId()).isEqualTo(9L);

        CompraFormalizacion vuelta = mapper.toDomain(entity);
        assertThat(vuelta.getNumero()).isEqualTo("OC-2026-000001");
        assertThat(vuelta.getMonto()).isEqualByComparingTo("28000.00");
        assertThat(vuelta.getObjeto()).isEqualTo("<p>Objeto</p>");
    }

    @Test
    void updateEntityAplicaLosCampos() {
        CompraFormalizacionEntity entity = CompraFormalizacionEntity.builder().build();
        mapper.updateEntity(domain(), entity);

        assertThat(entity.getNumero()).isEqualTo("OC-2026-000001");
        assertThat(entity.getNumeroExterno()).isEqualTo("OC-EXT");
        assertThat(entity.getUsuarioId()).isEqualTo(38);
    }

    @Test
    void itemIdaYVuelta() {
        CompraFormalizacionItem item = CompraFormalizacionItem.builder()
                .compraFormalizacionItemId(2L)
                .compraFormalizacionId(1L)
                .compraCotizacionItemId(9L)
                .compraPedidoItemId(100)
                .orden(1)
                .cantidad(new BigDecimal("3.00"))
                .unidad("Unidad")
                .descripcion("Notebook")
                .especificaciones("<p>16 GB</p>")
                .precioUnitario(new BigDecimal("9000.00"))
                .subtotal(new BigDecimal("27000.00"))
                .marca("ACME")
                .build();

        CompraFormalizacionItemEntity entity = mapper.itemToEntity(item);
        CompraFormalizacionItem vuelta = mapper.itemToDomain(entity);

        assertThat(vuelta.getCompraCotizacionItemId()).isEqualTo(9L);
        assertThat(vuelta.getDescripcion()).isEqualTo("Notebook");
        assertThat(vuelta.getPrecioUnitario()).isEqualByComparingTo("9000.00");
        assertThat(vuelta.getMarca()).isEqualTo("ACME");
    }

    @Test
    void nulosDevuelvenNulo() {
        assertThat(mapper.toEntity(null)).isNull();
        assertThat(mapper.toDomain(null)).isNull();
        assertThat(mapper.itemToEntity(null)).isNull();
        assertThat(mapper.itemToDomain(null)).isNull();
    }
}
