package um.tesoreria.core.service.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.exception.EjercicioBloqueadoException;
import um.tesoreria.core.hexagonal.contable.asiento.application.exception.AsientoException;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.kotlin.model.Ejercicio;
import um.tesoreria.core.service.EjercicioService;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/** Efecto del flag ejercicio.bloqueado en ContabilidadService (#408, E3). Simulación con Mockito, sin base. */
@ExtendWith(MockitoExtension.class)
class EjercicioBloqueadoConsumidoresTest {
    @Mock EjercicioService ejercicioService;
    @Mock AsientoService asientoService;
    @Mock CuentaMovimientoService cuentaMovimientoService;
    @InjectMocks ContabilidadService contabilidad;

    final OffsetDateTime fecha = OffsetDateTime.parse("2026-06-30T00:00:00Z");

    Ejercicio ejercicio(int bloqueado) {
        return new Ejercicio(15, "Ejercicio Económico 2026", OffsetDateTime.parse("2026-03-01T00:00:00Z"),
                OffsetDateTime.parse("2027-02-28T00:00:00Z"), (byte) bloqueado, 0, 0);
    }

    @Test
    void saveAsientoConEjercicioBloqueadoLanzaEjercicioBloqueadoYNoEscribe() {
        when(ejercicioService.findByFecha(fecha)).thenReturn(ejercicio(1));
        assertThatThrownBy(() -> contabilidad.saveAsiento(fecha, 1, null, "vinculo", List.of()))
                .isInstanceOf(EjercicioBloqueadoException.class);
        verifyNoInteractions(asientoService, cuentaMovimientoService);
    }

    @Test
    void saveAsientoConEjercicioAbiertoEscribeElAsiento() {
        when(ejercicioService.findByFecha(fecha)).thenReturn(ejercicio(0));
        when(asientoService.add(any())).thenAnswer(i -> i.getArgument(0));
        when(asientoService.findByAsiento(any(), any())).thenThrow(new AsientoException(fecha, 1));
        contabilidad.saveAsiento(fecha, 1, null, "vinculo", List.of());
        verify(asientoService).add(any());
    }

    @Test
    void deleteAsientoDesdeConEjercicioBloqueadoDevuelveFalseYNoBorra() {
        when(ejercicioService.findByFecha(fecha)).thenReturn(ejercicio(1));
        assertThat(contabilidad.deleteAsientoDesde(fecha, 1, 1)).isFalse();
        verify(asientoService, never()).deleteByAsientoId(any());
        verify(cuentaMovimientoService, never()).deleteAllByCuentaMovimientoIdIn(any());
    }

    @Test
    void deleteAsientoDesdeConEjercicioAbiertoBorraLosMovimientos() {
        when(ejercicioService.findByFecha(fecha)).thenReturn(ejercicio(0));
        when(asientoService.findByAsiento(fecha, 1)).thenThrow(new AsientoException(fecha, 1));
        assertThat(contabilidad.deleteAsientoDesde(fecha, 1, 1)).isTrue();
        verify(cuentaMovimientoService).deleteAllByCuentaMovimientoIdIn(List.of());
    }

    @Test
    void elMensajeDeEjercicioBloqueadoSePuedeConstruir() {
        assertThat(new EjercicioBloqueadoException(fecha).getMessage()).contains("Ejercicio Bloqueado");
    }
}
