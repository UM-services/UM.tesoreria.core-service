package um.tesoreria.core.service.facade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.contable.asiento.application.exception.AsientoException;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ReservarOrdenContableUseCase;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.exception.AsientoConflictoException;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.in.UpdateEjercicioUseCase;
import um.tesoreria.core.kotlin.model.Ejercicio;
import um.tesoreria.core.model.CoeficienteInflacion;
import um.tesoreria.core.service.CoeficienteInflacionService;
import um.tesoreria.core.service.EjercicioService;
import um.tesoreria.core.service.view.CuentaMensualService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsientoInflacionServiceTest {

    private static final Integer EJERCICIO = 15;
    private static final OffsetDateTime INICIO =
            OffsetDateTime.of(2026, 3, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime FIN =
            OffsetDateTime.of(2027, 2, 28, 0, 0, 0, 0, ZoneOffset.UTC);

    @Mock EjercicioService ejercicioService;
    @Mock UpdateEjercicioUseCase updateEjercicioUseCase;
    @Mock CuentaMensualService cuentaMensualService;
    @Mock CoeficienteInflacionService coeficienteInflacionService;
    @Mock CuentaMovimientoService cuentaMovimientoService;
    @Mock ReservarOrdenContableUseCase reservarOrdenContable;
    @Mock AsientoService asientoService;

    AsientoInflacionService service;

    @BeforeEach
    void preparar() {
        service = new AsientoInflacionService(ejercicioService, updateEjercicioUseCase, cuentaMensualService,
                coeficienteInflacionService, cuentaMovimientoService, reservarOrdenContable, asientoService);
    }

    private Ejercicio ejercicio(Integer ordenResultado, Integer ordenBienesUso) {
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setEjercicioId(EJERCICIO);
        ejercicio.setNombre("Ejercicio 2026");
        ejercicio.setFechaInicio(INICIO);
        ejercicio.setFechaFinal(FIN);
        ejercicio.setBloqueado((byte) 0);
        ejercicio.setOrdenContableResultado(ordenResultado);
        ejercicio.setOrdenContableBienesUso(ordenBienesUso);
        return ejercicio;
    }

    private void sinCuentasMensuales() {
        lenient().when(cuentaMensualService.findIngresosByMes(any(), any())).thenReturn(List.of());
        lenient().when(cuentaMensualService.findGastosByMes(any(), any())).thenReturn(List.of());
        lenient().when(cuentaMensualService.findBienesUsoByMes(any(), any())).thenReturn(List.of());
        lenient().when(coeficienteInflacionService.findByUnique(any(), any()))
                .thenReturn(new CoeficienteInflacion(null, 2026, 3, BigDecimal.ONE));
    }

    private void sinCabecera() {
        when(asientoService.findByAsiento(FIN, 7)).thenThrow(new AsientoException(FIN, 7));
    }

    @Test
    void resultadoSinOrdenReservaYGrabaCabecera() {
        when(ejercicioService.findByEjercicioId(EJERCICIO)).thenReturn(ejercicio(0, 0));
        when(reservarOrdenContable.reservar(FIN)).thenReturn(7);
        sinCuentasMensuales();
        sinCabecera();

        service.generateAsientoInflacionResultado(EJERCICIO);

        verify(reservarOrdenContable).reservar(FIN);
        verify(cuentaMovimientoService).deleteAsiento(FIN, 7, 1);
        ArgumentCaptor<AsientoEntity> cabecera = ArgumentCaptor.forClass(AsientoEntity.class);
        verify(asientoService).add(cabecera.capture());
        assertThat(cabecera.getValue().getVinculo()).isEqualTo("Asiento Inflación");
        assertThat(cabecera.getValue().getOrden()).isEqualTo(7);
        verify(cuentaMovimientoService, never()).createCuentaMovimiento(any());
    }

    @Test
    void resultadoConOrdenYaAsignadoNoReserva() {
        when(ejercicioService.findByEjercicioId(EJERCICIO)).thenReturn(ejercicio(93, 0));
        sinCuentasMensuales();
        when(asientoService.findByAsiento(FIN, 93)).thenThrow(new AsientoException(FIN, 93));

        service.generateAsientoInflacionResultado(EJERCICIO);

        verify(reservarOrdenContable, never()).reservar(any());
        verify(cuentaMovimientoService).deleteAsiento(FIN, 93, 1);
        ArgumentCaptor<AsientoEntity> cabecera = ArgumentCaptor.forClass(AsientoEntity.class);
        verify(asientoService).add(cabecera.capture());
        assertThat(cabecera.getValue().getOrden()).isEqualTo(93);
    }

    @Test
    void siElOrdenTieneOtroComprobanteAbortaSinCrearCabecera() {
        when(ejercicioService.findByEjercicioId(EJERCICIO)).thenReturn(ejercicio(93, 0));
        sinCuentasMensuales();
        doThrow(new AsientoConflictoException("otro comprobante"))
                .when(cuentaMovimientoService).deleteAsiento(FIN, 93, 1);

        assertThatThrownBy(() -> service.generateAsientoInflacionResultado(EJERCICIO))
                .isInstanceOf(AsientoConflictoException.class);

        verify(asientoService, never()).add(any());
    }

    @Test
    void bienesUsoSinOrdenReservaYGrabaCabecera() {
        when(ejercicioService.findByEjercicioId(EJERCICIO)).thenReturn(ejercicio(0, 0));
        when(reservarOrdenContable.reservar(FIN)).thenReturn(7);
        sinCuentasMensuales();
        sinCabecera();

        service.generateAsientoInflacionBienesUso(EJERCICIO);

        verify(reservarOrdenContable).reservar(FIN);
        verify(cuentaMovimientoService).deleteAsiento(FIN, 7, 1);
        ArgumentCaptor<AsientoEntity> cabecera = ArgumentCaptor.forClass(AsientoEntity.class);
        verify(asientoService).add(cabecera.capture());
        assertThat(cabecera.getValue().getVinculo()).isEqualTo("Asiento Inflación");
    }

}
