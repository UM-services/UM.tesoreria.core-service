package um.tesoreria.core.service.facade.mayor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import um.tesoreria.core.exception.ValorMovimientoException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.comprobante.application.service.ComprobanteService;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.kotlin.model.Ejercicio;
import um.tesoreria.core.service.EjercicioService;
import um.tesoreria.core.service.ProveedorPagoService;
import um.tesoreria.core.service.ProveedorValorService;
import um.tesoreria.core.service.ValorMovimientoService;
import um.tesoreria.core.service.ValorService;
import um.tesoreria.core.service.facade.ContabilidadService;
import um.tesoreria.core.service.facade.mayor.MayorConsultaException.Codigo;

/**
 * Reglas de la consulta del mayor. Todas las dependencias son simuladas: no hay base de datos ni red.
 */
class MayorConsultaServiceTest {

    private static final BigDecimal CAJA = new BigDecimal("10101010001");
    private static final BigDecimal BANCO = new BigDecimal("10102010001");
    private static final BigDecimal INTEGRADORA = new BigDecimal("10100000000");

    private EjercicioService ejercicioService;
    private CuentaService cuentaService;
    private ContabilidadService contabilidadService;
    private CuentaMovimientoService cuentaMovimientoService;
    private ComprobanteService comprobanteService;
    private ProveedorPagoService proveedorPagoService;
    private ValorMovimientoService valorMovimientoService;
    private MayorConsultaService service;

    private Ejercicio ejercicio2025;
    private Ejercicio ejercicio2026;

    @BeforeEach
    void setUp() {
        ejercicioService = mock(EjercicioService.class);
        cuentaService = mock(CuentaService.class);
        contabilidadService = mock(ContabilidadService.class);
        cuentaMovimientoService = mock(CuentaMovimientoService.class);
        comprobanteService = mock(ComprobanteService.class);
        proveedorPagoService = mock(ProveedorPagoService.class);
        valorMovimientoService = mock(ValorMovimientoService.class);
        service = new MayorConsultaService(ejercicioService, cuentaService, contabilidadService,
                cuentaMovimientoService, comprobanteService, proveedorPagoService, valorMovimientoService,
                mock(ProveedorValorService.class), mock(ProveedorMovimientoService.class), mock(ValorService.class));

        ejercicio2025 = ejercicio(25, "Ejercicio Económico 2025", fecha(2025, 3, 1), fecha(2026, 2, 28));
        ejercicio2026 = ejercicio(26, "Ejercicio Económico 2026", fecha(2026, 3, 1), fecha(2027, 2, 28));
        // el core entrega el listado del más nuevo al más viejo
        when(ejercicioService.findAll()).thenReturn(List.of(ejercicio2026, ejercicio2025));
        when(comprobanteService.findAll()).thenReturn(List.of(
                Comprobante.builder().comprobanteId(1).descripcion("Factura").build()));
        when(proveedorPagoService.findAllByFactura(anyLong())).thenReturn(List.of());
        when(valorMovimientoService.findFirstByContable(any(), anyInt()))
                .thenThrow(new ValorMovimientoException(fecha(2026, 1, 1), 1));
    }

    // protects=el saldo inicial parte de lo que informa el saldo inicial del ejercicio de desde y el saldo corre fila a fila; fails_when=el saldo arranca en cero, se acumula al revés o el final no es inicial + debe - haber; no_claim=no prueba el cálculo del saldo inicial (es de ContabilidadService)
    @Test
    void unaCuentaEnUnEjercicioCalculaSaldoInicialAcumuladoYFinal() {
        dado(cuenta(CAJA, 0), saldoInicial(CAJA, ejercicio2026, fecha(2026, 4, 1), "100.00", "40.00"),
                movimiento(CAJA, fecha(2026, 4, 10), 5, 1, "50.00"),
                movimiento(CAJA, fecha(2026, 4, 20), 6, 0, "30.00"));

        MayorConsultaResponse respuesta = service.consultar(
                pedido(fecha(2026, 4, 1), fecha(2026, 6, 30), List.of(CAJA), null, null));

        assertThat(respuesta.cuentas()).hasSize(1);
        MayorCuentaResponse caja = respuesta.cuentas().getFirst();
        assertThat(caja.saldoInicial().saldo()).isEqualByComparingTo("60.00");
        assertThat(caja.movimientos()).extracting(MayorMovimientoResponse::saldo)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactly(new BigDecimal("110.00"), new BigDecimal("80.00"));
        assertThat(caja.totalDebe()).isEqualByComparingTo("50.00");
        assertThat(caja.totalHaber()).isEqualByComparingTo("30.00");
        assertThat(caja.saldoFinal()).isEqualByComparingTo("80.00");
        assertThat(caja.movimientos().getFirst().haber()).isNull();
        assertThat(caja.movimientos().get(1).debe()).isNull();
        assertThat(caja.movimientos().getFirst().comprobante()).isEqualTo("Factura");
    }

    // protects=un rango que cruza el cierre trae movimientos de ambos ejercicios con su ejercicio y el saldo continúa corrido; fails_when=se rechaza el cruce, el saldo se reinicia al cambiar de ejercicio o el movimiento queda sin ejercicio; no_claim=no define si los asientos de cierre entran (lo decide el filtro apertura = 0 del repositorio)
    @Test
    void unRangoQueCruzaElCierreMantieneElSaldoCorridoEIdentificaElEjercicio() {
        dado(cuenta(CAJA, 0), saldoInicial(CAJA, ejercicio2025, fecha(2025, 12, 1), "0.00", "0.00"),
                movimiento(CAJA, fecha(2026, 2, 27), 8, 1, "100.00"),
                movimiento(CAJA, fecha(2026, 3, 2), 9, 1, "25.00"));

        MayorConsultaResponse respuesta = service.consultar(
                pedido(fecha(2025, 12, 1), fecha(2026, 3, 31), List.of(CAJA), null, null));

        List<MayorMovimientoResponse> movimientos = respuesta.cuentas().getFirst().movimientos();
        assertThat(movimientos).extracting(MayorMovimientoResponse::ejercicioId).containsExactly(25, 26);
        assertThat(movimientos).extracting(MayorMovimientoResponse::ejercicioNombre)
                .containsExactly("Ejercicio Económico 2025", "Ejercicio Económico 2026");
        assertThat(movimientos.get(1).saldo()).isEqualByComparingTo("125.00");
        // el saldo inicial es el del ejercicio de desde, no el de hasta
        verify(contabilidadService).saldoInicial(eq(CAJA), eq(ejercicio2025), any());
        verify(contabilidadService, never()).saldoInicial(any(), eq(ejercicio2026), any());
    }

    // protects=el período se consulta sin asientos de apertura, igual que el mayor actual; fails_when=se consulta con apertura 1 o sin filtro; no_claim=no prueba qué asientos tienen apertura = 0
    @Test
    void consultaLosMovimientosSinAsientosDeApertura() {
        dado(cuenta(CAJA, 0), saldoInicial(CAJA, ejercicio2026, fecha(2026, 4, 1), "0", "0"));

        service.consultar(pedido(fecha(2026, 4, 1), fecha(2026, 6, 30), List.of(CAJA), null, null));

        verify(cuentaMovimientoService).findAllByNumeroCuentaAndFechaContableBetweenAndApertura(
                CAJA, fecha(2026, 4, 1), fecha(2026, 6, 30), (byte) 0);
    }

    // protects=desde posterior a hasta se rechaza con DESDE_POSTERIOR sin consultar nada; fails_when=devuelve vacío o falla con otro error; no_claim=no prueba el estado HTTP (lo cubre el test del controlador)
    @Test
    void rechazaDesdePosteriorAHasta() {
        assertThatThrownBy(() -> service.consultar(
                pedido(fecha(2026, 6, 30), fecha(2026, 4, 1), List.of(CAJA), null, null)))
                .isInstanceOfSatisfying(MayorConsultaException.class,
                        e -> assertThat(e.getCodigo()).isEqualTo(Codigo.DESDE_POSTERIOR));
        verify(cuentaService, never()).findByNumeroCuenta(any());
    }

    // protects=sin fechas se rechaza con FECHAS_REQUERIDAS; fails_when=NullPointerException o consulta sin rango; no_claim=no prueba el formato de las fechas
    @Test
    void rechazaPedidoSinFechas() {
        assertThatThrownBy(() -> service.consultar(pedido(null, null, List.of(CAJA), null, null)))
                .isInstanceOfSatisfying(MayorConsultaException.class,
                        e -> assertThat(e.getCodigo()).isEqualTo(Codigo.FECHAS_REQUERIDAS));
    }

    // protects=un desde fuera de todo ejercicio o un hasta más allá del último se rechazan con FUERA_DE_EJERCICIO; fails_when=el servicio calcula saldos sobre fechas sin ejercicio; no_claim=no prueba el texto del mensaje
    @Test
    void rechazaFechasFueraDeTodoEjercicio() {
        assertThatThrownBy(() -> service.consultar(
                pedido(fecha(2024, 1, 1), fecha(2024, 2, 1), List.of(CAJA), null, null)))
                .isInstanceOfSatisfying(MayorConsultaException.class,
                        e -> assertThat(e.getCodigo()).isEqualTo(Codigo.FUERA_DE_EJERCICIO));
        assertThatThrownBy(() -> service.consultar(
                pedido(fecha(2026, 4, 1), fecha(2028, 1, 1), List.of(CAJA), null, null)))
                .isInstanceOfSatisfying(MayorConsultaException.class,
                        e -> assertThat(e.getCodigo()).isEqualTo(Codigo.FUERA_DE_EJERCICIO));
    }

    // protects=un hueco entre dos ejercicios del rango se rechaza con EJERCICIOS_NO_CONSECUTIVOS; fails_when=se salta el ejercicio faltante y se devuelve un mayor con un tramo sin cubrir; no_claim=no prueba rangos de tres o más ejercicios válidos
    @Test
    void rechazaEjerciciosNoConsecutivos() {
        Ejercicio ejercicio2028 = ejercicio(28, "Ejercicio Económico 2028", fecha(2028, 3, 1), fecha(2029, 2, 28));
        when(ejercicioService.findAll()).thenReturn(List.of(ejercicio2028, ejercicio2026, ejercicio2025));

        assertThatThrownBy(() -> service.consultar(
                pedido(fecha(2026, 4, 1), fecha(2028, 4, 1), List.of(CAJA), null, null)))
                .isInstanceOfSatisfying(MayorConsultaException.class,
                        e -> assertThat(e.getCodigo()).isEqualTo(Codigo.EJERCICIOS_NO_CONSECUTIVOS));
    }

    // protects=una lista de cuentas devuelve un bloque por cuenta ordenado por número y sin repetidos; fails_when=respeta el orden del pedido, repite una cuenta o devuelve menos bloques; no_claim=no prueba la cantidad máxima (no hay límite)
    @Test
    void unaListaDeCuentasDevuelveUnBloquePorCuentaOrdenado() {
        dado(cuenta(CAJA, 0), saldoInicial(CAJA, ejercicio2026, fecha(2026, 4, 1), "0", "0"));
        dado(cuenta(BANCO, 0), saldoInicial(BANCO, ejercicio2026, fecha(2026, 4, 1), "0", "0"));

        MayorConsultaResponse respuesta = service.consultar(pedido(fecha(2026, 4, 1), fecha(2026, 6, 30),
                List.of(BANCO, CAJA, new BigDecimal("10102010001.0")), null, null));

        assertThat(respuesta.cuentas()).extracting(MayorCuentaResponse::numeroCuenta)
                .containsExactly(CAJA, BANCO);
    }

    // protects=un rango de cuentas incluye solo cuentas imputables del intervalo, ordenadas; fails_when=entra una integradora, una cuenta fuera del intervalo o el orden cambia; no_claim=la regla "solo imputables" está pendiente de confirmar en la issue
    @Test
    void unRangoDeCuentasIncluyeSoloLasImputablesDelIntervalo() {
        Cuenta cajaCuenta = cuenta(CAJA, 0);
        Cuenta bancoCuenta = cuenta(BANCO, 0);
        Cuenta integradora = cuenta(INTEGRADORA, 1);
        Cuenta fuera = cuenta(new BigDecimal("20101010001"), 0);
        when(cuentaService.findAll()).thenReturn(List.of(fuera, bancoCuenta, integradora, cajaCuenta));
        dado(cajaCuenta, saldoInicial(CAJA, ejercicio2026, fecha(2026, 4, 1), "0", "0"));
        dado(bancoCuenta, saldoInicial(BANCO, ejercicio2026, fecha(2026, 4, 1), "0", "0"));

        MayorConsultaResponse respuesta = service.consultar(pedido(fecha(2026, 4, 1), fecha(2026, 6, 30),
                null, new BigDecimal("10100000000"), new BigDecimal("10199999999")));

        assertThat(respuesta.cuentas()).extracting(MayorCuentaResponse::numeroCuenta)
                .containsExactly(CAJA, BANCO);
    }

    // protects=un rango sin cuentas imputables se rechaza con RANGO_SIN_CUENTAS; fails_when=devuelve una respuesta vacía con 200; no_claim=no define qué es imputable en la base real
    @Test
    void unRangoSinCuentasSeRechaza() {
        when(cuentaService.findAll()).thenReturn(List.of(cuenta(INTEGRADORA, 1)));

        assertThatThrownBy(() -> service.consultar(pedido(fecha(2026, 4, 1), fecha(2026, 6, 30),
                null, new BigDecimal("10100000000"), new BigDecimal("10199999999"))))
                .isInstanceOfSatisfying(MayorConsultaException.class,
                        e -> assertThat(e.getCodigo()).isEqualTo(Codigo.RANGO_SIN_CUENTAS));
    }

    // protects=pedir lista y rango a la vez, o ninguno, se rechaza con SELECCION_INVALIDA; fails_when=se elige un modo en silencio; no_claim=no prueba la validación de tipos del cuerpo JSON
    @Test
    void rechazaSeleccionAmbiguaOVacia() {
        assertThatThrownBy(() -> service.consultar(pedido(fecha(2026, 4, 1), fecha(2026, 6, 30),
                List.of(CAJA), CAJA, BANCO)))
                .isInstanceOfSatisfying(MayorConsultaException.class,
                        e -> assertThat(e.getCodigo()).isEqualTo(Codigo.SELECCION_INVALIDA));
        assertThatThrownBy(() -> service.consultar(pedido(fecha(2026, 4, 1), fecha(2026, 6, 30),
                null, null, null)))
                .isInstanceOfSatisfying(MayorConsultaException.class,
                        e -> assertThat(e.getCodigo()).isEqualTo(Codigo.SELECCION_INVALIDA));
        assertThatThrownBy(() -> service.consultar(pedido(fecha(2026, 4, 1), fecha(2026, 6, 30),
                null, BANCO, CAJA)))
                .isInstanceOfSatisfying(MayorConsultaException.class,
                        e -> assertThat(e.getCodigo()).isEqualTo(Codigo.SELECCION_INVALIDA));
    }

    // protects=una lista de cuentas con cuentaDesde y cuentaHasta en 0 (lo que completa Swagger) se toma como lista y no como selección ambigua; fails_when=se rechaza con SELECCION_INVALIDA; no_claim=no cambia que un rango real junto con una lista siga rechazándose
    @Test
    void ceroEnElRangoDeCuentasSeTomaComoNoEnviado() {
        dado(cuenta(CAJA, 0), saldoInicial(CAJA, ejercicio2026, fecha(2026, 4, 1), "0", "0"));

        MayorConsultaResponse respuesta = service.consultar(pedido(fecha(2026, 4, 1), fecha(2026, 6, 30),
                List.of(CAJA), BigDecimal.ZERO, BigDecimal.ZERO));

        assertThat(respuesta.cuentas()).extracting(MayorCuentaResponse::numeroCuenta).containsExactly(CAJA);
    }

    // protects=una cuenta inexistente en la lista se rechaza con CUENTA_INEXISTENTE y nombra la cuenta; fails_when=se omite en silencio; no_claim=no prueba el estado HTTP 404
    @Test
    void rechazaUnaCuentaInexistente() {
        when(cuentaService.findByNumeroCuenta(CAJA)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.consultar(pedido(fecha(2026, 4, 1), fecha(2026, 6, 30),
                List.of(CAJA), null, null)))
                .isInstanceOfSatisfying(MayorConsultaException.class, e -> {
                    assertThat(e.getCodigo()).isEqualTo(Codigo.CUENTA_INEXISTENTE);
                    assertThat(e.getMessage()).contains("10101010001");
                });
    }

    // protects=un movimiento sin orden de pago ni valor sale con esos datos en null, sin romper; fails_when=NullPointerException o textos "null"; no_claim=no prueba el armado de la orden de pago ni del valor cuando existen
    @Test
    void unMovimientoSinOrdenDePagoNiValorDejaEsosDatosVacios() {
        dado(cuenta(CAJA, 0), saldoInicial(CAJA, ejercicio2026, fecha(2026, 4, 1), "0", "0"),
                movimiento(CAJA, fecha(2026, 4, 10), 5, 1, "10.00"));

        MayorMovimientoResponse linea = service.consultar(
                        pedido(fecha(2026, 4, 1), fecha(2026, 6, 30), List.of(CAJA), null, null))
                .cuentas().getFirst().movimientos().getFirst();

        assertThat(linea.ordenPago()).isNull();
        assertThat(linea.beneficiario()).isNull();
        assertThat(linea.ordenPagoValor()).isNull();
        assertThat(linea.valor()).isNull();
        assertThat(linea.numeroValor()).isNull();
        assertThat(linea.importeValor()).isNull();
    }

    // protects=si el período arranca el primer día del ejercicio, el asiento de apertura es el primer movimiento y el saldo inicial es cero (como en el listado anterior); fails_when=la apertura no aparece, se cuenta dos veces (en el saldo inicial y como línea) o se pide el saldo inicial al servicio; no_claim=no prueba cuándo un asiento tiene apertura = 1 (lo decide el repositorio)
    @Test
    void siElPeriodoArrancaConElEjercicioLaAperturaEsElPrimerMovimientoYElSaldoInicialEsCero() {
        dado(cuenta(CAJA, 0), saldoInicial(CAJA, ejercicio2026, fecha(2026, 3, 1), "999.00", "0.00"),
                movimiento(CAJA, fecha(2026, 3, 2), 5, 1, "10.00"));
        when(cuentaMovimientoService.findAllByNumeroCuentaAndFechaContableBetweenAndApertura(
                eq(CAJA), any(), any(), eq((byte) 1)))
                .thenReturn(List.of(movimiento(CAJA, fecha(2026, 3, 1), 1, 1, "100.00")));

        MayorCuentaResponse caja = service.consultar(
                pedido(fecha(2026, 3, 1), fecha(2026, 3, 31), List.of(CAJA), null, null)).cuentas().getFirst();

        assertThat(caja.saldoInicial().saldo()).isEqualByComparingTo("0.00");
        assertThat(caja.movimientos()).extracting(MayorMovimientoResponse::ordenContable).containsExactly(1, 5);
        assertThat(caja.movimientos()).extracting(MayorMovimientoResponse::saldo)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactly(new BigDecimal("100.00"), new BigDecimal("110.00"));
        assertThat(caja.totalDebe()).isEqualByComparingTo("110.00");
        assertThat(caja.saldoFinal()).isEqualByComparingTo("110.00");
        verify(contabilidadService, never()).saldoInicial(any(), any(), any());
    }

    // protects=si el período arranca después del inicio del ejercicio la apertura no se lista y va en el saldo inicial; fails_when=se consulta la apertura como movimiento y se duplica; no_claim=no prueba el cálculo del saldo inicial
    @Test
    void siElPeriodoArrancaDespuesDelInicioLaAperturaNoSeListaComoMovimiento() {
        dado(cuenta(CAJA, 0), saldoInicial(CAJA, ejercicio2026, fecha(2026, 4, 1), "100.00", "0.00"));

        service.consultar(pedido(fecha(2026, 4, 1), fecha(2026, 6, 30), List.of(CAJA), null, null));

        verify(cuentaMovimientoService, never()).findAllByNumeroCuentaAndFechaContableBetweenAndApertura(
                any(), any(), any(), eq((byte) 1));
    }

    // ---- ayudas ----

    private void dado(Cuenta cuenta, List<BigDecimal> iniciales, CuentaMovimiento... movimientos) {
        when(cuentaService.findByNumeroCuenta(cuenta.getNumeroCuenta())).thenReturn(Optional.of(cuenta));
        when(contabilidadService.saldoInicial(eq(cuenta.getNumeroCuenta()), any(Ejercicio.class), any()))
                .thenReturn(iniciales);
        when(cuentaMovimientoService.findAllByNumeroCuentaAndFechaContableBetweenAndApertura(
                eq(cuenta.getNumeroCuenta()), any(), any(), eq((byte) 0))).thenReturn(List.of(movimientos));
    }

    private static List<BigDecimal> saldoInicial(BigDecimal cuenta, Ejercicio ejercicio, OffsetDateTime desde,
                                                 String debe, String haber) {
        return List.of(new BigDecimal(debe), new BigDecimal(haber));
    }

    private static Cuenta cuenta(BigDecimal numero, int integradora) {
        return Cuenta.builder().numeroCuenta(numero).nombre("Cuenta " + numero.toPlainString())
                .integradora((byte) integradora).build();
    }

    private static CuentaMovimiento movimiento(BigDecimal cuenta, OffsetDateTime fecha, int orden, int debita,
                                               String importe) {
        return CuentaMovimiento.builder().numeroCuenta(cuenta).fechaContable(fecha).ordenContable(orden)
                .debita((byte) debita).importe(new BigDecimal(importe)).comprobanteId(1).concepto("Concepto " + orden)
                .proveedorMovimientoId(100L + orden).build();
    }

    private static Ejercicio ejercicio(int id, String nombre, OffsetDateTime inicio, OffsetDateTime fin) {
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setEjercicioId(id);
        ejercicio.setNombre(nombre);
        ejercicio.setFechaInicio(inicio);
        ejercicio.setFechaFinal(fin);
        return ejercicio;
    }

    private static MayorConsultaRequest pedido(OffsetDateTime desde, OffsetDateTime hasta, List<BigDecimal> cuentas,
                                               BigDecimal cuentaDesde, BigDecimal cuentaHasta) {
        return new MayorConsultaRequest(desde, hasta, cuentas, cuentaDesde, cuentaHasta);
    }

    private static OffsetDateTime fecha(int anio, int mes, int dia) {
        return OffsetDateTime.of(anio, mes, dia, 0, 0, 0, 0, ZoneOffset.UTC);
    }
}