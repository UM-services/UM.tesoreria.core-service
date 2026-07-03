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
class FindByProductoUseCaseImplTest {

    @Mock
    private LectivoTotalImputacionRepository repository;

    @InjectMocks
    private FindByProductoUseCaseImpl useCase;

    @Test
    void findByProducto_delegatesToRepository() {
        var domain = LectivoTotalImputacion.builder().build();
        when(repository.findByProducto(1, 2, 3, 4)).thenReturn(Optional.of(domain));

        var result = useCase.findByProducto(1, 2, 3, 4);

        assertThat(result).contains(domain);
        verify(repository).findByProducto(1, 2, 3, 4);
    }

    @Test
    void findByProducto_whenNotFound_returnsEmpty() {
        when(repository.findByProducto(1, 2, 3, 4)).thenReturn(Optional.empty());

        var result = useCase.findByProducto(1, 2, 3, 4);

        assertThat(result).isEmpty();
        verify(repository).findByProducto(1, 2, 3, 4);
    }

}
