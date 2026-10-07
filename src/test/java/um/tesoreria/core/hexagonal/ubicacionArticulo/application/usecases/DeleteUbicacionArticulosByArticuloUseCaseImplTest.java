package um.tesoreria.core.hexagonal.ubicacionArticulo.application.usecases;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.out.UbicacionArticuloRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DeleteUbicacionArticulosByArticuloUseCaseImplTest {

    private final UbicacionArticuloRepository repository = mock(UbicacionArticuloRepository.class);
    private final DeleteUbicacionArticulosByArticuloUseCaseImpl useCase = new DeleteUbicacionArticulosByArticuloUseCaseImpl(repository);

    @Test
    void devuelveLosVinculosBorrados() {
        var borrados = List.of(UbicacionArticulo.builder().ubicacionArticuloId(1L).ubicacionId(3).articuloId(5L).build());
        when(repository.deleteAllByArticuloId(5L)).thenReturn(borrados);

        assertThat(useCase.deleteByArticuloId(5L)).isEqualTo(borrados);
    }

    @Test
    void soloCorreDentroDeLaTransaccionDeLaBaja() throws Exception {
        var tx = DeleteUbicacionArticulosByArticuloUseCaseImpl.class.getMethod("deleteByArticuloId", Long.class)
                .getAnnotation(Transactional.class);

        assertThat(tx.propagation()).isEqualTo(Propagation.MANDATORY);
    }
}
