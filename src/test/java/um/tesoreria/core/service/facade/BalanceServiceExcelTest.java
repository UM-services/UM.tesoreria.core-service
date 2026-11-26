package um.tesoreria.core.service.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import um.tesoreria.core.hexagonal.comprobante.application.service.ComprobanteService;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.kotlin.model.Ejercicio;
import um.tesoreria.core.service.ProveedorPagoService;
import um.tesoreria.core.service.ProveedorValorService;
import um.tesoreria.core.service.ValorMovimientoService;
import um.tesoreria.core.service.ValorService;
import um.tesoreria.core.service.facade.mayor.MayorConsultaException;
import um.tesoreria.core.service.facade.mayor.MayorConsultaException.Codigo;
import um.tesoreria.core.service.facade.sumasysaldos.SumasSaldosFila;
import um.tesoreria.core.service.facade.sumasysaldos.SumasSaldosService;

/** Excel de sumas y saldos y del mayor: rechazo fuera de ejercicio y un libro propio por pedido. Sin base de datos. */
class BalanceServiceExcelTest {

    private static final int RANGOS = 6;
    private static final int PEDIDOS_POR_RANGO = 5;

    private CuentaService cuentas;
    private ContabilidadService contabilidad;
    private SumasSaldosService sumasSaldos;
    private BalanceService service;
    private Ejercicio ejercicio;

    @BeforeEach
    void setUp() {
        cuentas = mock(CuentaService.class);
        contabilidad = mock(ContabilidadService.class);
        sumasSaldos = mock(SumasSaldosService.class);
        service = new BalanceService(cuentas, contabilidad, mock(CuentaMovimientoService.class),
                mock(ComprobanteService.class), mock(ValorMovimientoService.class), mock(ProveedorValorService.class),
                mock(ProveedorMovimientoService.class), mock(ValorService.class), mock(ProveedorPagoService.class),
                sumasSaldos);
        ejercicio = new Ejercicio();
    }

    // protects=un rango que cruza ejercicios en el Excel de sumas y saldos se rechaza con FUERA_DE_EJERCICIO (409) como el JSON; fails_when=se devuelve un nombre de archivo de error o se arma el libro; no_claim=no prueba cuándo un rango cruza ejercicios (es de ContabilidadService)
    @Test
    void sumasSaldosFueraDeEjercicioSeRechaza() {
        OffsetDateTime desde = fecha(2025, 12, 1);
        OffsetDateTime hasta = fecha(2026, 2, 1);
        when(contabilidad.verifyEjercicio(desde, hasta)).thenReturn(null);

        assertThatThrownBy(() -> service.makeSumasSaldos(desde, hasta))
                .isInstanceOfSatisfying(MayorConsultaException.class, e -> {
                    assertThat(e.getCodigo()).isEqualTo(Codigo.FUERA_DE_EJERCICIO);
                    assertThat(e.getMessage()).isEqualTo("Desde y hasta deben pertenecer al mismo ejercicio");
                });
        verify(sumasSaldos, never()).filas(any(), any(), any());
    }

    // protects=un rango que cruza ejercicios en el Excel del mayor se rechaza con FUERA_DE_EJERCICIO (409); fails_when=se devuelve un nombre de archivo de error o se consultan saldos; no_claim=no prueba cuándo un rango cruza ejercicios
    @Test
    void rangoInvertidoSeRechazaComoEnElJson() {
        OffsetDateTime desde = fecha(2026, 3, 31);
        OffsetDateTime hasta = fecha(2026, 3, 1);

        assertThatThrownBy(() -> service.makeSumasSaldos(desde, hasta))
                .isInstanceOfSatisfying(MayorConsultaException.class,
                        e -> assertThat(e.getCodigo()).isEqualTo(Codigo.DESDE_POSTERIOR));
        assertThatThrownBy(() -> service.makeMayor(BigDecimal.ONE, desde, hasta))
                .isInstanceOfSatisfying(MayorConsultaException.class,
                        e -> assertThat(e.getCodigo()).isEqualTo(Codigo.DESDE_POSTERIOR));
        verify(contabilidad, never()).verifyEjercicio(any(), any());
    }

    @Test
    void cuentaInexistenteSeRechazaComoEnElJson() {
        OffsetDateTime desde = fecha(2026, 3, 1);
        OffsetDateTime hasta = fecha(2026, 3, 31);
        when(contabilidad.verifyEjercicio(desde, hasta)).thenReturn(ejercicio);

        assertThatThrownBy(() -> service.makeMayor(BigDecimal.valueOf(99999999999L), desde, hasta))
                .isInstanceOfSatisfying(MayorConsultaException.class,
                        e -> assertThat(e.getCodigo()).isEqualTo(Codigo.CUENTA_INEXISTENTE));
    }

    @Test
    void mayorFueraDeEjercicioSeRechaza() {
        OffsetDateTime desde = fecha(2025, 12, 1);
        OffsetDateTime hasta = fecha(2026, 2, 1);
        when(contabilidad.verifyEjercicio(desde, hasta)).thenReturn(null);

        assertThatThrownBy(() -> service.makeMayor(BigDecimal.ONE, desde, hasta))
                .isInstanceOfSatisfying(MayorConsultaException.class,
                        e -> assertThat(e.getCodigo()).isEqualTo(Codigo.FUERA_DE_EJERCICIO));
        verify(contabilidad, never()).saldoInicial(any(), any(), any());
    }

    // protects=C6: dos pedidos simultáneos no comparten estado y cada uno recibe su propio libro; fails_when=vuelve el archivo fijo compartido y un pedido recibe el libro de otro; no_claim=no reproduce el defecto anterior por sí solo (compara el estado de después contra sí mismo) ni compara bytes crudos, porque el xlsx lleva fecha de creación
    @Test
    void sumasSaldosSimultaneosDevuelvenCadaUnoSuLibro() throws Exception {
        for (int rango = 0; rango < RANGOS; rango++) {
            OffsetDateTime desde = fecha(2026, rango + 1, 1);
            OffsetDateTime hasta = fecha(2026, rango + 1, 28);
            when(contabilidad.verifyEjercicio(desde, hasta)).thenReturn(ejercicio);
            List<SumasSaldosFila> filas = new ArrayList<>();
            for (int cuenta = 0; cuenta <= rango * 40; cuenta++) {
                BigDecimal importe = BigDecimal.valueOf(rango * 1000L + cuenta);
                filas.add(new SumasSaldosFila(BigDecimal.valueOf(10101010000L + cuenta), "Cuenta " + rango + "-" + cuenta,
                        importe, BigDecimal.ZERO, importe, BigDecimal.ONE, importe, BigDecimal.ZERO));
            }
            when(sumasSaldos.filas(ejercicio, desde, hasta)).thenReturn(filas);
        }

        verificarSimultaneos(rango -> service.makeSumasSaldos(fecha(2026, rango + 1, 1), fecha(2026, rango + 1, 28)));
    }

    // protects=C6 en el mayor: dos pedidos simultáneos no comparten estado y cada uno recibe su propio libro; fails_when=vuelve el archivo fijo compartido; no_claim=no reproduce el defecto anterior por sí solo ni prueba el armado de líneas de movimientos (sin movimientos en el mock)
    @Test
    void mayoresSimultaneosDevuelvenCadaUnoSuLibro() throws Exception {
        for (int rango = 0; rango < RANGOS; rango++) {
            BigDecimal numeroCuenta = BigDecimal.valueOf(10101010000L + rango);
            OffsetDateTime desde = fecha(2026, rango + 1, 1);
            OffsetDateTime hasta = fecha(2026, rango + 1, 28);
            when(contabilidad.verifyEjercicio(desde, hasta)).thenReturn(ejercicio);
            when(cuentas.findByNumeroCuenta(numeroCuenta))
                    .thenReturn(Optional.of(Cuenta.builder().numeroCuenta(numeroCuenta).nombre("Cuenta " + rango).build()));
            when(contabilidad.saldoInicial(numeroCuenta, ejercicio, desde))
                    .thenReturn(List.of(BigDecimal.valueOf(rango * 100L), BigDecimal.valueOf(rango)));
        }

        verificarSimultaneos(rango -> service.makeMayor(BigDecimal.valueOf(10101010000L + rango),
                fecha(2026, rango + 1, 1), fecha(2026, rango + 1, 28)));
    }

    private static void verificarSimultaneos(Function<Integer, byte[]> pedido) throws Exception {
        List<List<List<Object>>> solos = new ArrayList<>();
        for (int rango = 0; rango < RANGOS; rango++) {
            solos.add(leer(pedido.apply(rango)));
        }
        assertThat(solos).doesNotHaveDuplicates();

        int total = RANGOS * PEDIDOS_POR_RANGO;
        ExecutorService pool = Executors.newFixedThreadPool(total);
        try {
            CountDownLatch largada = new CountDownLatch(1);
            List<Future<byte[]>> respuestas = new ArrayList<>();
            for (int i = 0; i < total; i++) {
                int rango = i % RANGOS;
                Callable<byte[]> tarea = () -> {
                    largada.await();
                    return pedido.apply(rango);
                };
                respuestas.add(pool.submit(tarea));
            }
            largada.countDown();
            for (int i = 0; i < total; i++) {
                assertThat(leer(respuestas.get(i).get(60, TimeUnit.SECONDS))).as("pedido %d", i)
                        .isEqualTo(solos.get(i % RANGOS));
            }
        } finally {
            pool.shutdownNow();
        }
    }

    private static List<List<Object>> leer(byte[] contenido) throws Exception {
        assertThat(contenido).isNotEmpty();
        try (var book = new XSSFWorkbook(new ByteArrayInputStream(contenido))) {
            assertThat(book.getNumberOfSheets()).isEqualTo(1);
            List<List<Object>> filas = new ArrayList<>();
            for (var row : book.getSheetAt(0)) {
                List<Object> valores = new ArrayList<>();
                for (var cell : row) {
                    valores.add(cell.getColumnIndex() + "=" + (cell.getCellType() == CellType.NUMERIC
                            ? cell.getNumericCellValue() : cell.getStringCellValue()));
                }
                filas.add(valores);
            }
            return filas;
        }
    }

    private static OffsetDateTime fecha(int anio, int mes, int dia) {
        return OffsetDateTime.of(anio, mes, dia, 0, 0, 0, 0, ZoneOffset.UTC);
    }
}
