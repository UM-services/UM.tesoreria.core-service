package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.model.AutorizanteEnvio;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.web.dto.AutorizanteEnvioResponse;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AutorizanteEnvioDtoMapperTest {

    private final AutorizanteEnvioDtoMapper mapper = new AutorizanteEnvioDtoMapper();

    @Test
    void toResponseArmaUsuarioYDependenciasOrdenadas() {
        AutorizanteEnvioResponse response = mapper.toResponse(new AutorizanteEnvio(9, Set.of(2, 1)));

        assertThat(response.getUsuarioId()).isEqualTo(9);
        assertThat(response.getDependenciaIds()).containsExactly(1, 2);
    }

    @Test
    void toResponseDeListaMantieneElOrden() {
        List<AutorizanteEnvioResponse> response = mapper.toResponse(List.of(
                new AutorizanteEnvio(9, Set.of(1)),
                new AutorizanteEnvio(10, Set.of(2))));

        assertThat(response).extracting(AutorizanteEnvioResponse::getUsuarioId).containsExactly(9, 10);
    }

}
