package um.tesoreria.core.service.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.in.UpdateEjercicioUseCase;
import um.tesoreria.core.kotlin.model.Ejercicio;
import um.tesoreria.core.model.CoeficienteInflacion;
import um.tesoreria.core.service.CoeficienteInflacionService;
import um.tesoreria.core.service.EjercicioService;
import um.tesoreria.core.service.view.CuentaMensualService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * El asiento de inflación solo cambia la orden contable: el resto (bloqueado incluido) va en null y
 * se conserva (#408, D10). Simulación con Mockito, sin base.
 */
@ExtendWith(MockitoExtension.class)
class AsientoInflacionEjercicioTest {
    @Mock EjercicioService ejercicioService;
    @Mock UpdateEjercicioUseCase updateEjercicio;
    @Mock CuentaMensualService cuentaMensualService;
    @Mock CoeficienteInflacionService coeficienteInflacionService;
    @Mock CuentaMovimientoService cuentaMovimientoService;
    @InjectMocks AsientoInflacionService service;

    void preparar() {
        // Ejercicio bloqueado: la actualización no puede tocar ese flag.
        when(ejercicioService.findByEjercicioId(15)).thenReturn(new Ejercicio(15, "Ejercicio Económico 2026",
                OffsetDateTime.parse("2026-03-01T00:00:00Z"), OffsetDateTime.parse("2026-03-31T00:00:00Z"),
                (byte) 1, 0, 0));
        CuentaMovimiento ultimo = new CuentaMovimiento();
        ultimo.setOrdenContable(40);
        when(cuentaMovimientoService.findLastByFecha(any())).thenReturn(ultimo);
        when(coeficienteInflacionService.findByUnique(anyInt(), anyInt()))
                .thenAnswer(i -> new CoeficienteInflacion(1L, i.getArgument(0), i.getArgument(1), BigDecimal.ONE));
    }

    @Test
    void resultadoActualizaSoloOrdenContableResultado() {
        preparar();
        when(cuentaMensualService.findIngresosByMes(anyInt(), anyInt())).thenReturn(List.of());
        when(cuentaMensualService.findGastosByMes(anyInt(), anyInt())).thenReturn(List.of());
        service.generateAsientoInflacionResultado(15);
        var cambios = ArgumentCaptor.forClass(um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio.class);
        verify(updateEjercicio).updateEjercicio(eq(15), cambios.capture());
        assertThat(cambios.getValue().getOrdenContableResultado()).isEqualTo(41);
        assertThat(cambios.getValue().getBloqueado()).isNull();
        assertThat(cambios.getValue().getOrdenContableBienesUso()).isNull();
        assertThat(cambios.getValue().getNombre()).isNull();
        assertThat(cambios.getValue().getFechaInicio()).isNull();
        assertThat(cambios.getValue().getFechaFinal()).isNull();
    }

    @Test
    void bienesUsoActualizaSoloOrdenContableBienesUso() {
        preparar();
        when(cuentaMensualService.findBienesUsoByMes(anyInt(), anyInt())).thenReturn(List.of());
        service.generateAsientoInflacionBienesUso(15);
        var cambios = ArgumentCaptor.forClass(um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio.class);
        verify(updateEjercicio).updateEjercicio(eq(15), cambios.capture());
        assertThat(cambios.getValue().getOrdenContableBienesUso()).isEqualTo(41);
        assertThat(cambios.getValue().getBloqueado()).isNull();
        assertThat(cambios.getValue().getOrdenContableResultado()).isNull();
    }
}
