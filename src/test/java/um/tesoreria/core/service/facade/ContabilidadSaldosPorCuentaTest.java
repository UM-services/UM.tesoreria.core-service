package um.tesoreria.core.service.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.SumaCuenta;
import um.tesoreria.core.kotlin.model.Ejercicio;

/** Saldos de todas las cuentas en tres consultas agrupadas. Sin base de datos: el servicio de movimientos es simulado. */
class ContabilidadSaldosPorCuentaTest {

    private static final BigDecimal CAJA = new BigDecimal("10101010001");
    private static final BigDecimal BANCO = new BigDecimal("10102010001");
    private static final OffsetDateTime INICIO = fecha(2026, 3, 1);

    private CuentaMovimientoService movimientos;
    private ContabilidadService contabilidad;
    private Ejercicio ejercicio;

    @BeforeEach
    void setUp() {
        movimientos = mock(CuentaMovimientoService.class);
        contabilidad = new ContabilidadService(null, null, null, movimientos, null, null, null, null);
        ejercicio = new Ejercicio();
        ejercicio.setFechaInicio(INICIO);
        ejercicio.setFechaFinal(fecha(2027, 2, 28));
    }

    // protects=el saldo inicial es la apertura más los movimientos sin apertura hasta el día anterior a desde, y el período no los repite; fails_when=la apertura se pierde, el período se suma al inicial o se consulta con otra apertura; no_claim=no prueba la consulta SQL (agrupa en el adaptador)
    @Test
    void elInicialSumaAperturaYMovimientosAnterioresYElPeriodoVaAparte() {
        OffsetDateTime desde = fecha(2026, 4, 1);
        OffsetDateTime hasta = fecha(2026, 4, 30);
        when(movimientos.sumarPorCuenta(INICIO, INICIO, (byte) 1))
                .thenReturn(List.of(new SumaCuenta(CAJA, new BigDecimal("100.00"), new BigDecimal("40.00"))));
        when(movimientos.sumarPorCuenta(INICIO, fecha(2026, 3, 31), (byte) 0))
                .thenReturn(List.of(new SumaCuenta(CAJA, new BigDecimal("5.00"), new BigDecimal("2.00"))));
        when(movimientos.sumarPorCuenta(desde, hasta, (byte) 0))
                .thenReturn(List.of(new SumaCuenta(CAJA, new BigDecimal("30.00"), new BigDecimal("10.00"))));

        Map<BigDecimal, SaldosCuenta> saldos = contabilidad.saldosPorCuenta(ejercicio, desde, hasta);

        SaldosCuenta caja = saldos.get(ContabilidadService.claveCuenta(CAJA));
        assertThat(caja.inicialDebitos()).isEqualByComparingTo("105.00");
        assertThat(caja.inicialCreditos()).isEqualByComparingTo("42.00");
        assertThat(caja.debitos()).isEqualByComparingTo("30.00");
        assertThat(caja.creditos()).isEqualByComparingTo("10.00");
    }

    // protects=una cuenta que sólo se movió en el período no arrastra saldo inicial y otra que sólo tiene apertura no tiene movimientos; fails_when=se mezclan los grupos entre cuentas; no_claim=no prueba cuentas sin movimientos (no figuran en el mapa)
    @Test
    void cadaCuentaTomaSoloLoSuyo() {
        OffsetDateTime desde = fecha(2026, 4, 1);
        OffsetDateTime hasta = fecha(2026, 4, 30);
        when(movimientos.sumarPorCuenta(INICIO, INICIO, (byte) 1))
                .thenReturn(List.of(new SumaCuenta(BANCO, new BigDecimal("50.00"), BigDecimal.ZERO)));
        when(movimientos.sumarPorCuenta(INICIO, fecha(2026, 3, 31), (byte) 0)).thenReturn(List.of());
        when(movimientos.sumarPorCuenta(desde, hasta, (byte) 0))
                .thenReturn(List.of(new SumaCuenta(CAJA, new BigDecimal("7.00"), BigDecimal.ZERO)));

        Map<BigDecimal, SaldosCuenta> saldos = contabilidad.saldosPorCuenta(ejercicio, desde, hasta);

        assertThat(saldos).hasSize(2);
        SaldosCuenta caja = saldos.get(ContabilidadService.claveCuenta(CAJA));
        assertThat(caja.inicialDebitos()).isEqualByComparingTo("0");
        assertThat(caja.debitos()).isEqualByComparingTo("7.00");
        SaldosCuenta banco = saldos.get(ContabilidadService.claveCuenta(BANCO));
        assertThat(banco.inicialDebitos()).isEqualByComparingTo("50.00");
        assertThat(banco.debitos()).isEqualByComparingTo("0");
    }

    // protects=cuando desde es el primer día del ejercicio el inicial es sólo la apertura y no se consulta el tramo anterior; fails_when=se consulta un rango invertido o se cuenta dos veces el primer día; no_claim=no prueba el período
    @Test
    void siDesdeEsElInicioDelEjercicioNoHayTramoAnterior() {
        OffsetDateTime hasta = fecha(2026, 3, 31);
        when(movimientos.sumarPorCuenta(any(), any(), any())).thenReturn(List.of());

        contabilidad.saldosPorCuenta(ejercicio, INICIO, hasta);

        verify(movimientos).sumarPorCuenta(INICIO, INICIO, (byte) 1);
        verify(movimientos).sumarPorCuenta(INICIO, hasta, (byte) 0);
        verify(movimientos, never()).sumarPorCuenta(eq(INICIO), eq(fecha(2026, 2, 28)), eq((byte) 0));
    }

    private static OffsetDateTime fecha(int anio, int mes, int dia) {
        return OffsetDateTime.of(anio, mes, dia, 0, 0, 0, 0, ZoneOffset.UTC);
    }
}