package um.tesoreria.core.service.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.exception.BancoMovimientoException;
import um.tesoreria.core.kotlin.model.Ejercicio;
import um.tesoreria.core.kotlin.model.ValorMovimiento;
import um.tesoreria.core.service.BancoMovimientoService;
import um.tesoreria.core.service.EjercicioService;
import um.tesoreria.core.service.ValorMovimientoService;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Efecto del flag ejercicio.bloqueado en CompraService.anulateValor (#408, E3). Simulación con Mockito, sin base. */
@ExtendWith(MockitoExtension.class)
class CompraServiceAnulateValorEjercicioTest {
    @Mock ValorMovimientoService valorMovimientoService;
    @Mock EjercicioService ejercicioService;
    @Mock ContabilidadService contabilidadService;
    @Mock BancoMovimientoService bancoMovimientoService;
    @InjectMocks CompraService compra;

    final OffsetDateTime fechaContable = OffsetDateTime.parse("2026-06-30T00:00:00Z");
    final OffsetDateTime anulacion = OffsetDateTime.parse("2026-10-21T00:00:00Z");

    void preparar(int bloqueado) {
        ValorMovimiento valor = new ValorMovimiento();
        valor.setValorMovimientoId(5L);
        valor.setFechaContable(fechaContable);
        valor.setOrdenContable(3);
        when(valorMovimientoService.findByValorMovimientoId(5L)).thenReturn(valor);
        when(valorMovimientoService.update(any(), any())).thenAnswer(i -> i.getArgument(0));
        when(ejercicioService.findByFecha(fechaContable)).thenReturn(new Ejercicio(15, "Ejercicio Económico 2026",
                OffsetDateTime.parse("2026-03-01T00:00:00Z"), OffsetDateTime.parse("2027-02-28T00:00:00Z"),
                (byte) bloqueado, 0, 0));
        when(bancoMovimientoService.findByValorMovimientoId(5L)).thenThrow(new BancoMovimientoException(5L));
    }

    @Test
    void ejercicioAbiertoEliminaElAsientoOriginal() {
        preparar(0);
        compra.anulateValor(5L, anulacion);
        verify(contabilidadService).deleteAsiento(fechaContable, 3);
        verify(contabilidadService, never()).contraAsiento(any(), any(), any());
    }

    @Test
    void ejercicioBloqueadoGeneraContraAsientoYConservaElOriginal() {
        preparar(1);
        compra.anulateValor(5L, anulacion);
        verify(contabilidadService).contraAsiento(eq(fechaContable), eq(3), any());
        verify(contabilidadService, never()).deleteAsiento(any(), any());
    }
}
