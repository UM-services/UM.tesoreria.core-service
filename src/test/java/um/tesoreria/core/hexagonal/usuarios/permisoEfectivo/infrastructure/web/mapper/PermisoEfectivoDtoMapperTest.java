package um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.model.PermisoEfectivo;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PermisoEfectivoDtoMapperTest {

    private final PermisoEfectivoDtoMapper mapper = new PermisoEfectivoDtoMapper();

    @Test
    void toResponse_mapsFields() {
        var response = mapper.toResponse(PermisoEfectivo.builder()
                .userId(1L).permisos(List.of("chequeras.imprimir")).build());

        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getPermisos()).containsExactly("chequeras.imprimir");
    }

    @Test
    void toResponse_whenNull_returnsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }
}
