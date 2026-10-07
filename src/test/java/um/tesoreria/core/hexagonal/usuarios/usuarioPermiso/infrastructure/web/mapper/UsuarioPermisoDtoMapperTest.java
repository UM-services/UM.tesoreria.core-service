package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.mapper.PermisoDtoMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.model.UsuarioPermiso;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioPermisoDtoMapperTest {

    private final UsuarioPermisoDtoMapper mapper = new UsuarioPermisoDtoMapper(new PermisoDtoMapper());

    @Test
    void toResponse_mapsScalarsAndNested() {
        var domain = UsuarioPermiso.builder()
                .usuarioPermisoId(1L).userId(1L).permisoId(10L).otorgado((byte) 1)
                .permiso(Permiso.builder().permisoId(10L).clave("pagos.aprobar").build())
                .build();

        var response = mapper.toResponse(domain);

        assertThat(response.getOtorgado()).isEqualTo((byte) 1);
        assertThat(response.getPermiso().getClave()).isEqualTo("pagos.aprobar");
    }
}
