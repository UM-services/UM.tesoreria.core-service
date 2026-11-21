package um.tesoreria.core.hexagonal.pagos.ordenPago.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ContextoArticulo;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ContextoFactura;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ContextoValor;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoContexto;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ReconstruccionAccion;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ReconstruccionAsiento;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ReconstruccionPlan;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Verifica el diferencial que calcula el plan de reconstrucción: el legacy reemplazaba todas las
 * líneas de cada asiento y por eso contaminaba comprobantes ajenos; acá sólo se agrega lo que
 * falta, se borra lo que sobra del propio comprobante y se avisa cuando el asiento es compartido.
 */
@ExtendWith(MockitoExtension.class)
class TrazaContableCalculatorTest {

    private static final OffsetDateTime REFERENCIA = OffsetDateTime.of(2026, 11, 21, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime FECHA_FACTURA = OffsetDateTime.of(2026, 11, 19, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final Long ORDEN_PAGO_ID = 104453L;
    private static final Long FACTURA_ID = 104452L;
    private static final Long VALOR_ID = 84190L;
    private static final BigDecimal IMPORTE_FACTURA = new BigDecimal("100.00");
    private static final BigDecimal IMPORTE_VALOR = new BigDecimal("10.00");
    private static final BigDecimal CUENTA_PROVEEDOR = new BigDecimal("20101090099");
    private static final BigDecimal CUENTA_ARTICULO = new BigDecimal("40102980004");
    private static final BigDecimal CUENTA_IMPUESTO = new BigDecimal("20101010009");
    private static final Integer COMPROBANTE_FACTURA = 4;
    private static final Integer COMPROBANTE_ORDEN_PAGO = 6;

    @Mock
    private ProveedorMovimientoService proveedorMovimientoService;

    @Mock
    private OrdenPagoPreparador ordenPagoPreparador;

    @Mock
    private CuentaMovimientoService cuentaMovimientoService;

    private TrazaContableCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new TrazaContableCalculator(proveedorMovimientoService, ordenPagoPreparador,
                cuentaMovimientoService);
    }

    @Test
    void noProponeCambiosCuandoLasLineasDelAsientoCoinciden() {
        ProveedorMovimiento ordenPago = ordenPago();
        when(proveedorMovimientoService.findByProveedorMovimientoId(ORDEN_PAGO_ID)).thenReturn(ordenPago);
        when(ordenPagoPreparador.contextoDe(ordenPago)).thenReturn(contexto(null, 20));
        stubComprobanteDeFactura(COMPROBANTE_FACTURA);
        when(cuentaMovimientoService.findAllByAsiento(FECHA_FACTURA, 20, 0, 2))
                .thenReturn(lineasFactura(COMPROBANTE_FACTURA));

        ReconstruccionPlan plan = calculator.calcular(ORDEN_PAGO_ID);

        assertThat(plan.sinCambios()).isTrue();
        assertThat(plan.cantidadSinCambios()).isEqualTo(1);
        assertThat(plan.requiereForzar()).isFalse();
        assertThat(plan.asientos()).singleElement()
                .extracting(ReconstruccionAsiento::accion)
                .isEqualTo(ReconstruccionAccion.SIN_CAMBIOS);
    }

    @Test
    void proponeCrearElAsientoDeUnHijoQueTodaviaNoTieneOrdenContable() {
        ProveedorMovimiento ordenPago = ordenPago();
        when(proveedorMovimientoService.findByProveedorMovimientoId(ORDEN_PAGO_ID)).thenReturn(ordenPago);
        when(ordenPagoPreparador.contextoDe(ordenPago)).thenReturn(contexto(VALOR_ID, 20));
        stubComprobanteDeFactura(COMPROBANTE_FACTURA);
        when(cuentaMovimientoService.findAllByAsiento(FECHA_FACTURA, 20, 0, 2))
                .thenReturn(lineasFactura(COMPROBANTE_FACTURA));

        ReconstruccionPlan plan = calculator.calcular(ORDEN_PAGO_ID);

        assertThat(plan.cantidadCrear()).isEqualTo(1);
        assertThat(plan.cantidadSinCambios()).isEqualTo(1);
        assertThat(plan.sinCambios()).isFalse();
        assertThat(plan.requiereForzar()).isFalse();
        assertThat(plan.asientos()).hasSize(2);
        assertThat(plan.asientos().getFirst().accion()).isEqualTo(ReconstruccionAccion.SIN_CAMBIOS);
        assertThat(plan.asientos().get(1)).satisfies(asiento -> {
            assertThat(asiento.accion()).isEqualTo(ReconstruccionAccion.CREAR);
            assertThat(asiento.ordenContable()).isNull();
            assertThat(asiento.fechaContable()).isEqualTo(REFERENCIA);
            assertThat(asiento.lineasExistentes()).isZero();
            assertThat(asiento.compartido()).isFalse();
            assertThat(asiento.aAgregar()).hasSize(2);
            assertThat(asiento.idsEliminar()).isEmpty();
        });
    }

    @Test
    void proponeActualizarCuandoSobraUnaLineaPropiaYExigeForzarPorSerAsientoCompartido() {
        ProveedorMovimiento ordenPago = ordenPago();
        when(proveedorMovimientoService.findByProveedorMovimientoId(ORDEN_PAGO_ID)).thenReturn(ordenPago);
        when(ordenPagoPreparador.contextoDe(ordenPago)).thenReturn(contexto(null, 20));
        stubComprobanteDeFactura(COMPROBANTE_FACTURA);
        when(cuentaMovimientoService.findAllByAsiento(FECHA_FACTURA, 20, 0, 2))
                .thenReturn(lineasFacturaConSobrante());

        ReconstruccionPlan plan = calculator.calcular(ORDEN_PAGO_ID);

        assertThat(plan.cantidadActualizar()).isEqualTo(1);
        assertThat(plan.sinCambios()).isFalse();
        assertThat(plan.requiereForzar()).isTrue();
        assertThat(plan.asientos()).singleElement().satisfies(asiento -> {
            assertThat(asiento.accion()).isEqualTo(ReconstruccionAccion.ACTUALIZAR);
            assertThat(asiento.compartido()).isTrue();
            assertThat(asiento.aAgregar()).isEmpty();
            assertThat(asiento.idsEliminar()).containsExactly(99L);
            assertThat(asiento.lineasExistentes()).isEqualTo(3);
        });
    }

    @Test
    void noTocaLasLineasDeOtrosComprobantesDelMismoAsiento() {
        ProveedorMovimiento ordenPago = ordenPago();
        when(proveedorMovimientoService.findByProveedorMovimientoId(ORDEN_PAGO_ID)).thenReturn(ordenPago);
        when(ordenPagoPreparador.contextoDe(ordenPago)).thenReturn(contexto(VALOR_ID, 20));
        stubComprobanteDeFactura(COMPROBANTE_FACTURA);
        when(cuentaMovimientoService.findAllByAsiento(FECHA_FACTURA, 20, 0, 2))
                .thenReturn(lineasFacturaConAjena());

        ReconstruccionPlan plan = calculator.calcular(ORDEN_PAGO_ID);

        assertThat(plan.cantidadSinCambios()).isEqualTo(1);
        assertThat(plan.cantidadCrear()).isEqualTo(1);
        assertThat(plan.sinCambios()).isFalse();
        assertThat(plan.requiereForzar()).isFalse();
        assertThat(plan.asientos()).hasSize(2);
        assertThat(plan.asientos().getFirst().compartido()).isTrue();
        assertThat(plan.asientos().getFirst().idsEliminar()).isEmpty();
        assertThat(plan.asientos().get(1).accion()).isEqualTo(ReconstruccionAccion.CREAR);
    }

    private ProveedorMovimiento ordenPago() {
        return ProveedorMovimiento.builder()
                .proveedorMovimientoId(ORDEN_PAGO_ID)
                .proveedorId(4484)
                .comprobanteId(COMPROBANTE_ORDEN_PAGO)
                .prefijo(15)
                .numeroComprobante(4169L)
                .importe(IMPORTE_VALOR)
                .fechaComprobante(REFERENCIA)
                .build();
    }

    /**
     * Contexto con una factura ya contabilizada (orden {@code ordenFactura}) y, opcionalmente, un
     * valor con asiento propio ({@code valorId} + orden 7) o sin asiento (orden {@code null}).
     */
    private OrdenPagoContexto contexto(Long valorId, Integer ordenFactura) {
        List<ContextoValor> valores = valorId == null
                ? List.of()
                : List.of(new ContextoValor(valorId, 6, IMPORTE_VALOR, REFERENCIA, null, null,
                        CUENTA_IMPUESTO, 0));
        return new OrdenPagoContexto(4484, COMPROBANTE_ORDEN_PAGO, (byte) 1, CUENTA_PROVEEDOR, 15, 4169L,
                REFERENCIA, ORDEN_PAGO_ID,
                List.of(new ContextoFactura(FACTURA_ID, COMPROBANTE_FACTURA, (byte) 0, 4484, IMPORTE_FACTURA,
                        CUENTA_PROVEEDOR, ordenFactura == null ? null : FECHA_FACTURA, ordenFactura,
                        List.of(new ContextoArticulo(CUENTA_ARTICULO, IMPORTE_FACTURA)))),
                List.of(), valores);
    }

    private void stubComprobanteDeFactura(Integer comprobanteId) {
        lenient().when(proveedorMovimientoService.findByProveedorMovimientoId(FACTURA_ID))
                .thenReturn(ProveedorMovimiento.builder()
                        .proveedorMovimientoId(FACTURA_ID)
                        .comprobanteId(comprobanteId)
                        .build());
    }

    private List<CuentaMovimiento> lineasFactura(Integer comprobanteId) {
        return List.of(
                linea(1L, 1, CUENTA_PROVEEDOR, (byte) 0, IMPORTE_FACTURA, comprobanteId),
                linea(2L, 2, CUENTA_ARTICULO, (byte) 1, IMPORTE_FACTURA, comprobanteId));
    }

    private List<CuentaMovimiento> lineasFacturaConSobrante() {
        return List.of(
                linea(1L, 1, CUENTA_PROVEEDOR, (byte) 0, IMPORTE_FACTURA, COMPROBANTE_FACTURA),
                linea(2L, 2, CUENTA_ARTICULO, (byte) 1, IMPORTE_FACTURA, COMPROBANTE_FACTURA),
                linea(99L, 3, CUENTA_ARTICULO, (byte) 1, new BigDecimal("5.00"), COMPROBANTE_FACTURA),
                linea(77L, 4, new BigDecimal("60101010001"), (byte) 1, new BigDecimal("7.00"), 99));
    }

    private List<CuentaMovimiento> lineasFacturaConAjena() {
        return List.of(
                linea(1L, 1, CUENTA_PROVEEDOR, (byte) 0, IMPORTE_FACTURA, COMPROBANTE_FACTURA),
                linea(2L, 2, CUENTA_ARTICULO, (byte) 1, IMPORTE_FACTURA, COMPROBANTE_FACTURA),
                linea(3L, 3, CUENTA_ARTICULO, (byte) 1, new BigDecimal("5.00"), 99));
    }

    private CuentaMovimiento linea(Long id, Integer item, BigDecimal cuenta, Byte debita, BigDecimal importe,
                                   Integer comprobanteId) {
        return CuentaMovimiento.builder()
                .cuentaMovimientoId(id)
                .item(item)
                .numeroCuenta(cuenta)
                .debita(debita)
                .importe(importe)
                .comprobanteId(comprobanteId)
                .build();
    }
}
