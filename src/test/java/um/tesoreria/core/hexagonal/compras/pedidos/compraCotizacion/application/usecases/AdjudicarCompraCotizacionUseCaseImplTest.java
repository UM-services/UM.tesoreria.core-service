package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.exception.CompraCotizacionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacionDecision;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraAdjudicacionRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdjudicarCompraCotizacionUseCaseImplTest {

    @Mock private CompraCotizacionRepository compraCotizacionRepository;
    @Mock private CompraAdjudicacionRepository compraAdjudicacionRepository;

    @InjectMocks
    private AdjudicarCompraCotizacionUseCaseImpl useCase;

    private CompraCotizacion proceso(CompraCotizacionEstado estado) {
        return CompraCotizacion.builder().compraCotizacionId(1L).ejercicioId(15).estado(estado).build();
    }

    @Test
    void adjudicaCambiaElEstadoYGuardaElActo() {
        CompraCotizacion proceso = proceso(CompraCotizacionEstado.RECOMENDADA);
        when(compraCotizacionRepository.findById(1L)).thenReturn(Optional.of(proceso));
        when(compraAdjudicacionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        CompraAdjudicacion resultado = useCase.adjudicar(1L, CompraAdjudicacionDecision.ADJUDICADA, 5L, 7,
                new BigDecimal("28000.00"), "menor total", 38L, new BigDecimal("300000.00"));

        assertThat(proceso.getEstado()).isEqualTo(CompraCotizacionEstado.ADJUDICADA);
        assertThat(resultado.getDecision()).isEqualTo(CompraAdjudicacionDecision.ADJUDICADA);
        assertThat(resultado.getEjercicioId()).isEqualTo(15);
        verify(compraCotizacionRepository).updateHeader(proceso);
        verify(compraAdjudicacionRepository).save(any());
    }

    @Test
    void noRecomendadoFalla() {
        when(compraCotizacionRepository.findById(1L)).thenReturn(Optional.of(proceso(CompraCotizacionEstado.ENVIADA)));

        assertThatThrownBy(() -> useCase.adjudicar(1L, CompraAdjudicacionDecision.ADJUDICADA, null, null,
                null, null, 38L, null)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void inexistenteFalla() {
        when(compraCotizacionRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.adjudicar(9L, CompraAdjudicacionDecision.NO_ADJUDICADA, null, null,
                null, null, 38L, null)).isInstanceOf(CompraCotizacionException.class);
    }
}
