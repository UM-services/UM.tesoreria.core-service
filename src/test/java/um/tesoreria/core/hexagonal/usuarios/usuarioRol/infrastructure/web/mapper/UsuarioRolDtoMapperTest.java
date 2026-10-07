package um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.mapper.RolDtoMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.model.UsuarioRol;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioRolDtoMapperTest {

    private final UsuarioRolDtoMapper mapper = new UsuarioRolDtoMapper(new RolDtoMapper());

    @Test
    void toResponse_mapsScalarsAndNested() {
        var domain = UsuarioRol.builder()
                .usuarioRolId(1L).userId(1L).rolId(5L)
                .rol(Rol.builder().rolId(5L).nombre("OPERADOR_CHEQUERAS").build())
                .build();

        var response = mapper.toResponse(domain);

        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getRol().getNombre()).isEqualTo("OPERADOR_CHEQUERAS");
    }
}
