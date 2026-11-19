package um.tesoreria.core.service.facade.sumasysaldos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.kotlin.model.Ejercicio;
import um.tesoreria.core.service.facade.ContabilidadService;
import um.tesoreria.core.service.facade.SaldosCuenta;
import um.tesoreria.core.service.facade.mayor.MayorConsultaException;
import um.tesoreria.core.service.facade.mayor.MayorConsultaException.Codigo;

/** Reglas del balance de sumas y saldos. Dependencias simuladas: no hay base de datos. */
class SumasSaldosServiceTest {

    private static final BigDecimal CAJA = new BigDecimal("10101010001");
    private static final BigDecimal PROVEEDORES = new BigDecimal("21101010001");
    private static final BigDecimal SIN_MOVIMIENTOS = new BigDecimal("31101010001");
    private static final OffsetDateTime DESDE = fecha(2026, 4, 1);
    private static final OffsetDateTime HASTA = fecha(2026, 4, 30);

    private ContabilidadService contabilidad;
    private CuentaService cuentas;
    private SumasSaldosService service;
    private Ejercicio ejercicio;

    @BeforeEach
    void setUp() {
        contabilidad = mock(ContabilidadService.class);
        cuentas = mock(CuentaService.class);
        service = new SumasSaldosService(contabilidad, cuentas);
        ejercicio = new Ejercicio();
        when(contabilidad.verifyEjercicio(DESDE, HASTA)).thenReturn(ejercicio);
        when(cuentas.findAllGrado5()).thenReturn(List.of(cuenta(CAJA, "Caja"), cuenta(PROVEEDORES, "Proveedores"),
                cuenta(SIN_MOVIMIENTOS, "Sin movimientos")));
        when(contabilidad.saldosPorCuenta(ejercicio, DESDE, HASTA)).thenReturn(Map.of(
                ContabilidadService.claveCuenta(CAJA),
                new SaldosCuenta(new BigDecimal("100.50"), new BigDecimal("20.25"), new BigDecimal("30.25"), new BigDecimal("10")),
                ContabilidadService.claveCuenta(PROVEEDORES),
                new SaldosCuenta(BigDecimal.ZERO, new BigDecimal("80"), new BigDecimal("10"), new BigDecimal("40"))));
    }

    // protects=saldo = (inicial debe - inicial haber) + (debe - haber): positivo es deudor y negativo acreedor, una cuenta sin movimientos sale en cero; fails_when=se invierte el signo, se pierde el inicial o la cuenta sin movimientos desaparece; no_claim=no prueba el origen de los importes (es de ContabilidadService)
    @Test
    void cadaCuentaSaleConSuSaldoDeudorOAcreedor() {
        SumasSaldosResponse respuesta = service.consultar(new SumasSaldosRequest(DESDE, HASTA));

        assertThat(respuesta.cuentas()).extracting(SumasSaldosFila::nombre)
                .containsExactly("Caja", "Proveedores", "Sin movimientos");
        SumasSaldosFila caja = respuesta.cuentas().get(0);
        assertThat(caja.saldoDeudor()).isEqualByComparingTo("100.50");
        assertThat(caja.saldoAcreedor()).isEqualByComparingTo("0");
        SumasSaldosFila proveedores = respuesta.cuentas().get(1);
        assertThat(proveedores.saldoDeudor()).isEqualByComparingTo("0");
        assertThat(proveedores.saldoAcreedor()).isEqualByComparingTo("110");
        SumasSaldosFila vacia = respuesta.cuentas().get(2);
        assertThat(vacia.inicialDebitos()).isEqualByComparingTo("0");
        assertThat(vacia.saldoDeudor()).isEqualByComparingTo("0");
        assertThat(vacia.saldoAcreedor()).isEqualByComparingTo("0");
    }

    // protects=los totales suman cada columna sobre todas las filas; fails_when=un total omite una cuenta o mezcla columnas; no_claim=no prueba que el balance cuadre (depende de los datos)
    @Test
    void losTotalesSumanCadaColumna() {
        SumasSaldosResponse.Totales totales = service.consultar(new SumasSaldosRequest(DESDE, HASTA)).totales();

        assertThat(totales.inicialDebitos()).isEqualByComparingTo("100.50");
        assertThat(totales.inicialCreditos()).isEqualByComparingTo("100.25");
        assertThat(totales.debitos()).isEqualByComparingTo("40.25");
        assertThat(totales.creditos()).isEqualByComparingTo("50");
        assertThat(totales.saldoDeudor()).isEqualByComparingTo("100.50");
        assertThat(totales.saldoAcreedor()).isEqualByComparingTo("110");
    }

    // protects=las fechas faltantes, invertidas o fuera de un ejercicio se rechazan con su código y no se consulta nada; fails_when=se calcula igual o se responde con otro código; no_claim=no prueba el mensaje exacto
    @Test
    void rechazaFechasInvalidas() {
        assertThatThrownBy(() -> service.consultar(new SumasSaldosRequest(null, HASTA)))
                .isInstanceOfSatisfying(MayorConsultaException.class, e -> assertThat(e.getCodigo()).isEqualTo(Codigo.FECHAS_REQUERIDAS));
        assertThatThrownBy(() -> service.consultar(new SumasSaldosRequest(HASTA, DESDE)))
                .isInstanceOfSatisfying(MayorConsultaException.class, e -> assertThat(e.getCodigo()).isEqualTo(Codigo.DESDE_POSTERIOR));
        OffsetDateTime otraHasta = fecha(2027, 4, 30);
        when(contabilidad.verifyEjercicio(DESDE, otraHasta)).thenReturn(null);
        assertThatThrownBy(() -> service.consultar(new SumasSaldosRequest(DESDE, otraHasta)))
                .isInstanceOfSatisfying(MayorConsultaException.class, e -> assertThat(e.getCodigo()).isEqualTo(Codigo.FUERA_DE_EJERCICIO));
        verify(contabilidad, never()).saldosPorCuenta(any(), any(), any());
    }

    private static Cuenta cuenta(BigDecimal numero, String nombre) {
        return Cuenta.builder().numeroCuenta(numero).nombre(nombre).grado(5).build();
    }

    private static OffsetDateTime fecha(int anio, int mes, int dia) {
        return OffsetDateTime.of(anio, mes, dia, 0, 0, 0, 0, ZoneOffset.UTC);
    }
}