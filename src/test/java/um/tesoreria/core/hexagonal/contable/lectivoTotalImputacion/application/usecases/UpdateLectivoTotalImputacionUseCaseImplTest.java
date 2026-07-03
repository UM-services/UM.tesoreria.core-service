package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.out.LectivoTotalImputacionRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateLectivoTotalImputacionUseCaseImplTest {

    @Mock
    private LectivoTotalImputacionRepository repository;

    @InjectMocks
    private UpdateLectivoTotalImputacionUseCaseImpl useCase;

    @Test
    void update_delegatesToRepository() {
        var domain = LectivoTotalImputacion.builder().build();
        when(repository.update(1L, domain)).thenReturn(Optional.of(domain));

        var result = useCase.update(1L, domain);

        assertThat(result).contains(domain);
        verify(repository).update(1L, domain);
    }

    @Test
    void update_whenNotFound_returnsEmpty() {
        var domain = LectivoTotalImputacion.builder().build();
        when(repository.update(1L, domain)).thenReturn(Optional.empty());

        var result = useCase.update(1L, domain);

        assertThat(result).isEmpty();
        verify(repository).update(1L, domain);
    }

}
