package um.tesoreria.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.exception.TipoImpresionException;
import um.tesoreria.core.model.TipoImpresion;
import um.tesoreria.core.repository.TipoImpresionRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoImpresionServiceTest {

    @Mock
    private TipoImpresionRepository repository;

    @InjectMocks
    private TipoImpresionService service;

    @Test
    void findByTipoImpresionId_returnsTheTipoImpresionWhenItExists() {
        TipoImpresion rapipago = new TipoImpresion();
        rapipago.setTipoImpresionId(1);
        rapipago.setNombre("Rapipago");
        when(repository.findByTipoImpresionId(1)).thenReturn(Optional.of(rapipago));

        TipoImpresion result = service.findByTipoImpresionId(1);

        assertThat(result.getNombre()).isEqualTo("Rapipago");
    }

    @Test
    void findByTipoImpresionId_throwsWhenNotFound() {
        when(repository.findByTipoImpresionId(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByTipoImpresionId(99))
                .isInstanceOf(TipoImpresionException.class);
    }

}