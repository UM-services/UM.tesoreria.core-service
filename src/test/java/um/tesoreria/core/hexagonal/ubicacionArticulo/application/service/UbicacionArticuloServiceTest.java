package um.tesoreria.core.hexagonal.ubicacionArticulo.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloConflictException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloValidationException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in.GetAllUbicacionArticulosUseCase;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in.GetUbicacionArticuloUseCase;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in.GetUbicacionArticulosByArticuloUseCase;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in.SaveUbicacionArticuloUseCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Los choques son simulados: el caso de uso es un mock que lanza la excepción ya traducida. */
class UbicacionArticuloServiceTest {

    private SaveUbicacionArticuloUseCase save;
    private UbicacionArticuloService service;
    private final UbicacionArticulo pedido = UbicacionArticulo.builder().ubicacionId(1).articuloId(2L).build();

    @BeforeEach
    void setUp() {
        save = mock(SaveUbicacionArticuloUseCase.class);
        service = new UbicacionArticuloService(mock(GetAllUbicacionArticulosUseCase.class), save,
                mock(GetUbicacionArticuloUseCase.class), mock(GetUbicacionArticulosByArticuloUseCase.class));
    }

    @Test
    void choqueReintentable_seReintentaUnaVezEnOtraLlamada() {
        var guardado = UbicacionArticulo.builder().ubicacionArticuloId(9L).build();
        when(save.save(pedido)).thenThrow(new UbicacionArticuloConflictException(true, "1062")).thenReturn(guardado);

        assertThat(service.save(pedido)).isSameAs(guardado);
        verify(save, times(2)).save(pedido);
    }

    @Test
    void segundoChoque_salePorConflictoSinTercerIntento() {
        when(save.save(pedido)).thenThrow(new UbicacionArticuloConflictException(true, "1213"));

        assertThatThrownBy(() -> service.save(pedido)).isInstanceOf(UbicacionArticuloConflictException.class);
        verify(save, times(2)).save(pedido);
    }

    @Test
    void conflictoNoReintentable_noSeReintenta() {
        when(save.save(pedido)).thenThrow(new UbicacionArticuloConflictException(false, "1062 desconocido"));

        assertThatThrownBy(() -> service.save(pedido)).isInstanceOf(UbicacionArticuloConflictException.class);
        verify(save, times(1)).save(pedido);
    }

    @Test
    void validacion_noSeReintenta() {
        when(save.save(pedido)).thenThrow(new UbicacionArticuloValidationException("ubicacionId", "no existe"));

        assertThatThrownBy(() -> service.save(pedido)).isInstanceOf(UbicacionArticuloValidationException.class);
        verify(save, times(1)).save(pedido);
    }
}
