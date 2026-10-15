package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.exception.CompraCotizacionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FormalizarCompraCotizacionUseCaseImplTest {

    @Mock private CompraCotizacionRepository compraCotizacionRepository;

    @InjectMocks
    private FormalizarCompraCotizacionUseCaseImpl useCase;

    @Test
    void formalizaElProcesoAdjudicado() {
        CompraCotizacion proceso = CompraCotizacion.builder()
                .compraCotizacionId(1L).estado(CompraCotizacionEstado.ADJUDICADA).build();
        when(compraCotizacionRepository.findById(1L)).thenReturn(Optional.of(proceso));
        when(compraCotizacionRepository.updateHeader(proceso)).thenReturn(proceso);

        CompraCotizacion resultado = useCase.formalizar(1L);

        assertThat(resultado.getEstado()).isEqualTo(CompraCotizacionEstado.FORMALIZADA);
        verify(compraCotizacionRepository).updateHeader(proceso);
    }

    @Test
    void procesoInexistenteFalla() {
        when(compraCotizacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.formalizar(99L)).isInstanceOf(CompraCotizacionException.class);
    }

    @Test
    void procesoNoAdjudicadoFalla() {
        CompraCotizacion proceso = CompraCotizacion.builder()
                .compraCotizacionId(1L).estado(CompraCotizacionEstado.RECOMENDADA).build();
        when(compraCotizacionRepository.findById(1L)).thenReturn(Optional.of(proceso));

        assertThatThrownBy(() -> useCase.formalizar(1L)).isInstanceOf(IllegalStateException.class);
    }
}
