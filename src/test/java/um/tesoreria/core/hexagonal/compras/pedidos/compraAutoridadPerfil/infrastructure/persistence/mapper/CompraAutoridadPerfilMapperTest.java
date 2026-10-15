package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.persistence.entity.CompraAutoridadPerfilEntity;

import static org.assertj.core.api.Assertions.assertThat;

class CompraAutoridadPerfilMapperTest {

    private final CompraAutoridadPerfilMapper mapper = new CompraAutoridadPerfilMapper();

    @Test
    void toDomainConservaMultiplicoNuloComoIlimitado() {
        CompraAutoridadPerfil domain = mapper.toDomain(CompraAutoridadPerfilEntity.builder()
                .autoridadPerfilId(1L).nombre("Ilimitado").multiplico(null).build());

        assertThat(domain.getMultiplico()).isNull();
        assertThat(domain.getActivo()).isEqualTo((byte) 1);
    }

    @Test
    void toEntityYDomainSonSimetricos() {
        CompraAutoridadPerfil domain = CompraAutoridadPerfil.builder()
                .autoridadPerfilId(1L).nombre("N1").multiplico(3).activo((byte) 0).build();

        CompraAutoridadPerfilEntity entity = mapper.toEntity(domain);

        assertThat(entity.getNombre()).isEqualTo("N1");
        assertThat(entity.getMultiplico()).isEqualTo(3);
        assertThat(entity.getActivo()).isEqualTo((byte) 0);
    }

    @Test
    void updateEntitySeteaMultiplicoAunqueSeaNulo() {
        CompraAutoridadPerfilEntity entity = CompraAutoridadPerfilEntity.builder()
                .autoridadPerfilId(1L).nombre("N1").multiplico(3).build();

        mapper.updateEntity(CompraAutoridadPerfil.builder().nombre("N1").multiplico(null).build(), entity);

        assertThat(entity.getMultiplico()).isNull();
    }

    @Test
    void mapeaNulos() {
        assertThat(mapper.toDomain(null)).isNull();
        assertThat(mapper.toEntity(null)).isNull();
        mapper.updateEntity(null, null);
    }

}
