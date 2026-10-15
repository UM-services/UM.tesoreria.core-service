package um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.mapper.PermisoDtoMapper;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.mapper.RolDtoMapper;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model.RolPermiso;

import static org.assertj.core.api.Assertions.assertThat;

class RolPermisoDtoMapperTest {

    private final RolPermisoDtoMapper mapper =
            new RolPermisoDtoMapper(new RolDtoMapper(), new PermisoDtoMapper());

    @Test
    void toResponse_mapsScalarsAndNested() {
        var domain = RolPermiso.builder()
                .rolPermisoId(1L).rolId(5L).permisoId(10L)
                .rol(Rol.builder().rolId(5L).nombre("OPERADOR_CHEQUERAS").build())
                .permiso(Permiso.builder().permisoId(10L).clave("chequeras.eliminar").build())
                .build();

        var response = mapper.toResponse(domain);

        assertThat(response.getRolId()).isEqualTo(5L);
        assertThat(response.getRol().getNombre()).isEqualTo("OPERADOR_CHEQUERAS");
        assertThat(response.getPermiso().getClave()).isEqualTo("chequeras.eliminar");
    }

    @Test
    void toResponse_whenNull_returnsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }
}
