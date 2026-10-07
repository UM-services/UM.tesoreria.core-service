package um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.entity.RolEntity;

import static org.assertj.core.api.Assertions.assertThat;

class RolMapperTest {

    private final RolMapper mapper = new RolMapper();

    @Test
    void toDomain_whenNull_returnsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    void toDomain_mapsFields() {
        var entity = RolEntity.builder()
                .rolId(1L).nombre("OPERADOR_CHEQUERAS").descripcion("Chequeras")
                .aplicacion("TESORERIA").activo((byte) 1).build();

        var domain = mapper.toDomain(entity);

        assertThat(domain.getRolId()).isEqualTo(1L);
        assertThat(domain.getNombre()).isEqualTo("OPERADOR_CHEQUERAS");
        assertThat(domain.getAplicacion()).isEqualTo("TESORERIA");
    }

    @Test
    void toEntity_mapsFields() {
        var entity = mapper.toEntity(Rol.builder().rolId(1L).nombre("ADMIN_TESORERIA").build());

        assertThat(entity.getRolId()).isEqualTo(1L);
        assertThat(entity.getNombre()).isEqualTo("ADMIN_TESORERIA");
        assertThat(entity.getActivo()).isEqualTo((byte) 1);
    }
}
