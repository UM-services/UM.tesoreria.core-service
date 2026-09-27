package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.model.UsuarioChequeraGeografica;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.ports.in.GetUsuarioChequeraGeograficasByUserIdUseCase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioChequeraGeograficaServiceTest {

    @Mock
    private GetUsuarioChequeraGeograficasByUserIdUseCase getUsuarioChequeraGeograficasByUserIdUseCase;

    @InjectMocks
    private UsuarioChequeraGeograficaService service;

    @Test
    void findAllByUserId_delegatesToUseCase() {
        var results = List.of(UsuarioChequeraGeografica.builder().build());
        when(getUsuarioChequeraGeograficasByUserIdUseCase.getByUserId(1L)).thenReturn(results);

        assertThat(service.findAllByUserId(1L)).isEqualTo(results);
        verify(getUsuarioChequeraGeograficasByUserIdUseCase).getByUserId(1L);
    }

}
