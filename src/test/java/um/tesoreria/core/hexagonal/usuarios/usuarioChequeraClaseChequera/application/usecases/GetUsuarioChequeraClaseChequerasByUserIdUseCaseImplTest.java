package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model.UsuarioChequeraClaseChequera;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.ports.out.UsuarioChequeraClaseChequeraRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUsuarioChequeraClaseChequerasByUserIdUseCaseImplTest {

    @Mock
    private UsuarioChequeraClaseChequeraRepository repository;

    @InjectMocks
    private GetUsuarioChequeraClaseChequerasByUserIdUseCaseImpl useCase;

    @Test
    void getByUserId_delegatesToRepository() {
        var results = List.of(UsuarioChequeraClaseChequera.builder().build());
        when(repository.findAllByUserId(1L)).thenReturn(results);

        var actual = useCase.getByUserId(1L);

        assertThat(actual).isEqualTo(results);
        verify(repository).findAllByUserId(1L);
    }

}
