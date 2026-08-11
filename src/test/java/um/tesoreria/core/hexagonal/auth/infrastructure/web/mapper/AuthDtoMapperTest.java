package um.tesoreria.core.hexagonal.auth.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;
import um.tesoreria.core.hexagonal.dependencias.geografica.application.service.GeograficaService;
import um.tesoreria.core.hexagonal.dependencias.geografica.domain.model.Geografica;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthDtoMapperTest {

    @Mock
    private GeograficaService geograficaService;

    @InjectMocks
    private AuthDtoMapper mapper;

    @Test
    void toResponse_whenDomainIsNull_returnsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    void toResponse_mapsAllFields() {
        var geografica = new Geografica();
        geografica.setGeograficaId(7);
        geografica.setNombre("Sede Centro");
        when(geograficaService.findByGeograficaId(7)).thenReturn(Optional.of(geografica));

        var usuario = new UsuarioAuth();
        usuario.setUserId(1L);
        usuario.setNombre("Daniel");
        usuario.setGeograficaId(7);

        var response = mapper.toResponse(usuario);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("Daniel");
        assertThat(response.getGeograficaId()).isEqualTo(7);
        assertThat(response.getSede()).isEqualTo("Sede Centro");
    }

    @Test
    void toResponse_whenGeograficaNotFound_usesFallbackSede() {
        when(geograficaService.findByGeograficaId(7)).thenReturn(Optional.empty());

        var usuario = new UsuarioAuth();
        usuario.setUserId(1L);
        usuario.setGeograficaId(7);

        var response = mapper.toResponse(usuario);

        assertThat(response).isNotNull();
        assertThat(response.getGeograficaId()).isEqualTo(7);
        assertThat(response.getSede()).isEqualTo("Sede no encontrada");
    }

}
