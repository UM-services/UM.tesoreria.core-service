package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.model.UsuarioChequeraGeografica;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.ports.out.UsuarioChequeraGeograficaRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUsuarioChequeraGeograficasByUserIdUseCaseImplTest {

    @Mock
    private UsuarioChequeraGeograficaRepository repository;

    @InjectMocks
    private GetUsuarioChequeraGeograficasByUserIdUseCaseImpl useCase;

    @Test
    void getByUserId_delegatesToRepository() {
        var results = List.of(UsuarioChequeraGeografica.builder().build());
        when(repository.findAllByUserId(1L)).thenReturn(results);

        var actual = useCase.getByUserId(1L);

        assertThat(actual).isEqualTo(results);
        verify(repository).findAllByUserId(1L);
    }

}
