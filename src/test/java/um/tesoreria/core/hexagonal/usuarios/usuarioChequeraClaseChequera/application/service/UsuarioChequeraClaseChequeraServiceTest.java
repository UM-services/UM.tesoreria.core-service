package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model.UsuarioChequeraClaseChequera;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.ports.in.GetUsuarioChequeraClaseChequerasByUserIdUseCase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioChequeraClaseChequeraServiceTest {

    @Mock
    private GetUsuarioChequeraClaseChequerasByUserIdUseCase getUsuarioChequeraClaseChequerasByUserIdUseCase;

    @InjectMocks
    private UsuarioChequeraClaseChequeraService service;

    @Test
    void findAllByUserId_delegatesToUseCase() {
        var results = List.of(UsuarioChequeraClaseChequera.builder().build());
        when(getUsuarioChequeraClaseChequerasByUserIdUseCase.getByUserId(1L)).thenReturn(results);

        assertThat(service.findAllByUserId(1L)).isEqualTo(results);
        verify(getUsuarioChequeraClaseChequerasByUserIdUseCase).getByUserId(1L);
    }

}
