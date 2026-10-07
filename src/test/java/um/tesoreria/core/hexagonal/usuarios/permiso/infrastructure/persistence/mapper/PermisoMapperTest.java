package um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.entity.PermisoEntity;

import static org.assertj.core.api.Assertions.assertThat;

class PermisoMapperTest {

    private final PermisoMapper mapper = new PermisoMapper();

    @Test
    void toDomain_whenNull_returnsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    void toDomain_mapsFields() {
        var entity = PermisoEntity.builder()
                .permisoId(1L)
                .clave("chequeras.eliminar")
                .descripcion("Eliminar chequeras")
                .modulo("chequeras")
                .aplicacion("TESORERIA")
                .activo((byte) 1)
                .build();

        var domain = mapper.toDomain(entity);

        assertThat(domain.getPermisoId()).isEqualTo(1L);
        assertThat(domain.getClave()).isEqualTo("chequeras.eliminar");
        assertThat(domain.getModulo()).isEqualTo("chequeras");
        assertThat(domain.getAplicacion()).isEqualTo("TESORERIA");
        assertThat(domain.getActivo()).isEqualTo((byte) 1);
    }

    @Test
    void toEntity_whenNull_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void toEntity_mapsFields() {
        var domain = Permiso.builder()
                .permisoId(1L)
                .clave("pagos.aprobar")
                .modulo("pagos")
                .build();

        var entity = mapper.toEntity(domain);

        assertThat(entity.getPermisoId()).isEqualTo(1L);
        assertThat(entity.getClave()).isEqualTo("pagos.aprobar");
        assertThat(entity.getAplicacion()).isEqualTo("TESORERIA");
        assertThat(entity.getActivo()).isEqualTo((byte) 1);
    }
}
