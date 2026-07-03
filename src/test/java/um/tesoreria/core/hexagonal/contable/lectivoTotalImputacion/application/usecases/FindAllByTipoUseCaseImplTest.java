package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.out.LectivoTotalImputacionRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindAllByTipoUseCaseImplTest {

    @Mock
    private LectivoTotalImputacionRepository repository;

    @InjectMocks
    private FindAllByTipoUseCaseImpl useCase;

    @Test
    void findAllByTipo_delegatesToRepository() {
        var results = List.of(LectivoTotalImputacion.builder().build());
        when(repository.findAllByTipo(1, 2, 3)).thenReturn(results);

        var actual = useCase.findAllByTipo(1, 2, 3);

        assertThat(actual).isEqualTo(results);
        verify(repository).findAllByTipo(1, 2, 3);
    }

}
