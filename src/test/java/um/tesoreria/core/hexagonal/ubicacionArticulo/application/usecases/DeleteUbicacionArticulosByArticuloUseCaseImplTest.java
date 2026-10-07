package um.tesoreria.core.hexagonal.ubicacionArticulo.application.usecases;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.in.RegistrarEscrituraHistorialUseCase;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.out.UbicacionArticuloRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class DeleteUbicacionArticulosByArticuloUseCaseImplTest {

    private final UbicacionArticuloRepository repository = mock(UbicacionArticuloRepository.class);
    private final RegistrarEscrituraHistorialUseCase historial = mock(RegistrarEscrituraHistorialUseCase.class);
    private final DeleteUbicacionArticulosByArticuloUseCaseImpl useCase = new DeleteUbicacionArticulosByArticuloUseCaseImpl(repository, historial);

    @Test
    void devuelveLosVinculosBorrados() {
        var borrados = List.of(UbicacionArticulo.builder().ubicacionArticuloId(1L).ubicacionId(3).articuloId(5L).build());
        when(repository.deleteAllByArticuloId(5L)).thenReturn(borrados);

        assertThat(useCase.deleteByArticuloId(5L)).isEqualTo(borrados);
    }

    @Test
    void cadaVinculoBorrado_registraSuBaja() {
        when(repository.deleteAllByArticuloId(5L)).thenReturn(List.of(
                UbicacionArticulo.builder().ubicacionArticuloId(1L).ubicacionId(3).articuloId(5L).numeroCuenta(new BigDecimal("51010000")).build(),
                UbicacionArticulo.builder().ubicacionArticuloId(2L).ubicacionId(4).articuloId(5L).build()));

        useCase.deleteByArticuloId(5L);

        verify(historial).registrarBaja("ubicacion_articulo", "3:5", new UbicacionArticuloEstado(1L, 3, 5L, new BigDecimal("51010000")));
        verify(historial).registrarBaja("ubicacion_articulo", "4:5", new UbicacionArticuloEstado(2L, 4, 5L, null));
    }

    @Test
    void sinVinculos_noRegistra() {
        when(repository.deleteAllByArticuloId(5L)).thenReturn(List.of());

        useCase.deleteByArticuloId(5L);

        verifyNoInteractions(historial);
    }

    @Test
    void soloCorreDentroDeLaTransaccionDeLaBaja() throws Exception {
        var tx = DeleteUbicacionArticulosByArticuloUseCaseImpl.class.getMethod("deleteByArticuloId", Long.class)
                .getAnnotation(Transactional.class);

        assertThat(tx.propagation()).isEqualTo(Propagation.MANDATORY);
    }
}
