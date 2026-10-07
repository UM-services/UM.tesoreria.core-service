package um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.dto.RolRequest;

import static org.assertj.core.api.Assertions.assertThat;

class RolDtoMapperTest {

    private final RolDtoMapper mapper = new RolDtoMapper();

    @Test
    void toDomain_mapsRequest() {
        var request = RolRequest.builder().nombre("OPERADOR_CHEQUERAS").descripcion("d").build();

        var domain = mapper.toDomain(request);

        assertThat(domain.getNombre()).isEqualTo("OPERADOR_CHEQUERAS");
        assertThat(domain.getAplicacion()).isEqualTo("TESORERIA");
    }

    @Test
    void toResponse_whenNull_returnsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    void toResponse_mapsFields() {
        var response = mapper.toResponse(Rol.builder().rolId(1L).nombre("OPERADOR_CHEQUERAS").build());

        assertThat(response.getRolId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("OPERADOR_CHEQUERAS");
        assertThat(response.getActivo()).isEqualTo((byte) 1);
    }
}
