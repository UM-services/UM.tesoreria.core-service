package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.persistence.entity.CompraReferenciaEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CompraReferenciaMapperTest {

    private final CompraReferenciaMapper mapper = new CompraReferenciaMapper();

    @Test
    void toDomainYToEntitySonSimetricos() {
        CompraReferenciaEntity entity = CompraReferenciaEntity.builder()
                .ejercicioId(7).importe(new BigDecimal("100.00")).build();

        CompraReferencia domain = mapper.toDomain(entity);

        assertThat(domain.getEjercicioId()).isEqualTo(7);
        assertThat(domain.getImporte()).isEqualByComparingTo("100.00");
        assertThat(mapper.toEntity(domain).getEjercicioId()).isEqualTo(7);
    }

    @Test
    void updateEntitySoloAplicaImporteNoNulo() {
        CompraReferenciaEntity entity = CompraReferenciaEntity.builder()
                .ejercicioId(7).importe(new BigDecimal("100.00")).build();

        mapper.updateEntity(CompraReferencia.builder().ejercicioId(7).build(), entity);

        assertThat(entity.getImporte()).isEqualByComparingTo("100.00");
        mapper.updateEntity(CompraReferencia.builder().ejercicioId(7).importe(new BigDecimal("250.00")).build(), entity);
        assertThat(entity.getImporte()).isEqualByComparingTo("250.00");
    }

    @Test
    void mapeaNulos() {
        assertThat(mapper.toDomain(null)).isNull();
        assertThat(mapper.toEntity(null)).isNull();
        mapper.updateEntity(null, null);
    }

}
