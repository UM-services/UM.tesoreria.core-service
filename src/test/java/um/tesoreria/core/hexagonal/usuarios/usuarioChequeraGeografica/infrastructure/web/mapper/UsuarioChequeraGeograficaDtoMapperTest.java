package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.dependencias.geografica.domain.model.Geografica;
import um.tesoreria.core.hexagonal.dependencias.geografica.infrastructure.web.mapper.GeograficaDtoMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.model.UsuarioChequeraGeografica;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.web.dto.UsuarioChequeraGeograficaResponse;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioChequeraGeograficaDtoMapperTest {

    private final UsuarioChequeraGeograficaDtoMapper mapper =
            new UsuarioChequeraGeograficaDtoMapper(new GeograficaDtoMapper());

    @Test
    void toResponse_whenDomainIsNull_returnsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    void toResponse_mapsScalarFields() {
        var domain = UsuarioChequeraGeografica.builder()
                .usuarioChequeraGeograficaId(1L)
                .userId(10L)
                .geograficaId(20)
                .build();

        var response = mapper.toResponse(domain);

        assertThat(response.getUsuarioChequeraGeograficaId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(10L);
        assertThat(response.getGeograficaId()).isEqualTo(20);
    }

    @Test
    void toResponse_propagatesGeografica() {
        var geografica = new Geografica(20, "Sede Norte", (byte) 0);
        var domain = UsuarioChequeraGeografica.builder()
                .usuarioChequeraGeograficaId(1L)
                .userId(10L)
                .geograficaId(20)
                .geografica(geografica)
                .build();

        var response = mapper.toResponse(domain);

        assertThat(response.getGeografica()).isNotNull();
        assertThat(response.getGeografica().getGeograficaId()).isEqualTo(20);
        assertThat(response.getGeografica().getNombre()).isEqualTo("Sede Norte");
    }

    @Test
    void toResponse_whenGeograficaIsNull_mapsNullNested() {
        var domain = UsuarioChequeraGeografica.builder()
                .usuarioChequeraGeograficaId(1L)
                .geograficaId(20)
                .build();

        var response = mapper.toResponse(domain);

        assertThat(response.getGeografica()).isNull();
    }

}
