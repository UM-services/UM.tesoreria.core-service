package um.tesoreria.core.hexagonal.dependencias.ubicacion.application.usecases;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.dependencias.ubicacion.domain.model.Ubicacion;
import um.tesoreria.core.hexagonal.dependencias.ubicacion.domain.ports.out.UbicacionRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetUbicacionByIdUseCaseImplTest {

    private final UbicacionRepository repository = mock(UbicacionRepository.class);
    private final GetUbicacionByIdUseCaseImpl useCase = new GetUbicacionByIdUseCaseImpl(repository);

    @Test
    void existente_seDevuelve() {
        var ubicacion = Ubicacion.builder().ubicacionId(1).nombre("Tesorería").build();
        when(repository.findById(1)).thenReturn(Optional.of(ubicacion));

        assertThat(useCase.getUbicacionById(1)).containsSame(ubicacion);
    }

    @Test
    void inexistente_vacio() {
        when(repository.findById(1051)).thenReturn(Optional.empty());

        assertThat(useCase.getUbicacionById(1051)).isEmpty();
    }
}
