package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.out.ChequeraSerieRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateChequeraSerieUseCaseImplTest {

    @Mock
    private ChequeraSerieRepository repository;

    @InjectMocks
    private UpdateChequeraSerieUseCaseImpl useCase;

    @Test
    void updateSinVersion_conservaLaVersionExistente() {
        ChequeraSerie actual = ChequeraSerie.builder().chequeraId(1L).version(3L).build();
        when(repository.findByChequeraId(1L)).thenReturn(Optional.of(actual));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ChequeraSerie cambios = ChequeraSerie.builder().facultadId(10).tipoChequeraId(2).build();

        ChequeraSerie result = useCase.update(cambios, 1L);

        assertThat(result.getVersion()).isEqualTo(3L);
    }

    @Test
    void updateConVersion_actualizaLaVersion() {
        ChequeraSerie actual = ChequeraSerie.builder().chequeraId(1L).version(3L).build();
        when(repository.findByChequeraId(1L)).thenReturn(Optional.of(actual));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ChequeraSerie cambios = ChequeraSerie.builder().facultadId(10).tipoChequeraId(2).version(4L).build();

        ChequeraSerie result = useCase.update(cambios, 1L);

        assertThat(result.getVersion()).isEqualTo(4L);
    }
}
