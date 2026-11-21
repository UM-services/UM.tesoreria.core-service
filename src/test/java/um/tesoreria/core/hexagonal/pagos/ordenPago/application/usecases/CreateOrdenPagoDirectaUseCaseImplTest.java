package um.tesoreria.core.hexagonal.pagos.ordenPago.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ReservarOrdenContableUseCase;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.application.service.BancoMovimientoService;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.model.BancoMovimiento;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.GetBancariaByIdUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.service.EjercicioService;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.exception.OrdenPagoValidacionException;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.model.OrdenPagoDirectaPreparado;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.service.OrdenPagoDirectaPreparador;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ContextoGasto;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ContextoValor;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoDirectaContexto;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoDirectaGasto;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoDirectaSolicitud;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoResultado;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoValor;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.application.service.ProveedorValorService;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.model.ProveedorValor;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.service.ValorMovimientoService;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.service.ValorService;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.Valor;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.ValorMovimiento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Paridad de {@code clsCtlCompras.saveDirecto}: reserva del encabezado, un asiento por valor,
 * gastos al debe con la obligación al haber, conn {@code comprobanteId = 1} y sin facturas.
 */
@ExtendWith(MockitoExtension.class)
class CreateOrdenPagoDirectaUseCaseImplTest {

    private static final OffsetDateTime FECHA = OffsetDateTime.of(2026, 11, 21, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final Long ORDEN_PAGO_ID = 104454L;
    private static final Integer PREFIJO = 15;
    private static final Long NUMERO = 4170L;
    private static final BigDecimal CUENTA_OBLIGACION = new BigDecimal("20101090099");
    private static final BigDecimal CUENTA_GASTO = new BigDecimal("40102980004");
    private static final BigDecimal CUENTA_IMPUESTO = new BigDecimal("20101010009");
    private static final BigDecimal CUENTA_BANCO = new BigDecimal("10102040001");
    private static final BigDecimal TOTAL = new BigDecimal("100.00");
    private static final BigDecimal IMPUESTO = new BigDecimal("10.00");
    private static final BigDecimal TRANSFERENCIA = new BigDecimal("90.00");
    private static final Long IMPUESTO_ID = 84190L;
    private static final Long TRANSFERENCIA_ID = 84191L;

    @Mock
    private OrdenPagoDirectaPreparador preparador;
    @Mock
    private ProveedorMovimientoService proveedorMovimientoService;
    @Mock
    private AsientoService asientoService;
    @Mock
    private CuentaMovimientoService cuentaMovimientoService;
    @Mock
    private ReservarOrdenContableUseCase reservarOrdenContable;
    @Mock
    private ValorMovimientoService valorMovimientoService;
    @Mock
    private ProveedorValorService proveedorValorService;
    @Mock
    private BancoMovimientoService bancoMovimientoService;
    @Mock
    private ValorService valorService;
    @Mock
    private GetBancariaByIdUseCase getBancariaByIdUseCase;
    @Mock
    private EjercicioService ejercicioService;

    private CreateOrdenPagoDirectaUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateOrdenPagoDirectaUseCaseImpl(preparador, proveedorMovimientoService, asientoService,
                cuentaMovimientoService, reservarOrdenContable, valorMovimientoService, proveedorValorService,
                bancoMovimientoService, valorService, getBancariaByIdUseCase, ejercicioService);
    }

    @Test
    void registraElEncabezadoConGastosYUnAsientoPorValor() {
        when(preparador.preparar(any())).thenReturn(preparado());
        when(reservarOrdenContable.reservar(FECHA)).thenReturn(3, 4, 5);
        when(proveedorMovimientoService.createProveedorMovimiento(any())).thenReturn(ordenPago());
        when(ejercicioService.findByFecha(LocalDate.of(2026, 11, 21))).thenReturn(Optional.of(ejercicio()));
        when(valorService.getValorById(6)).thenReturn(impuesto());
        when(valorService.getValorById(5)).thenReturn(transferencia());
        when(valorMovimientoService.create(any())).thenReturn(valorMovimiento(IMPUESTO_ID, 6, 4, IMPUESTO, null),
                valorMovimiento(TRANSFERENCIA_ID, 5, 5, TRANSFERENCIA, 8L));
        when(getBancariaByIdUseCase.getBancariaById(8L)).thenReturn(bancaria());
        when(bancoMovimientoService.nextOrden(8L, FECHA)).thenReturn(2L);

        OrdenPagoResultado resultado = useCase.createDirecta(solicitud());

        assertThat(resultado.proveedorMovimientoId()).isEqualTo(ORDEN_PAGO_ID);
        assertThat(resultado.prefijo()).isEqualTo(PREFIJO);
        assertThat(resultado.numeroComprobante()).isEqualTo(NUMERO);

        // Orden de pago: sin proveedor, con asiento propio y el importe de los gastos.
        ArgumentCaptor<ProveedorMovimiento> ordenPago = ArgumentCaptor.forClass(ProveedorMovimiento.class);
        verify(proveedorMovimientoService).createProveedorMovimiento(ordenPago.capture());
        assertThat(ordenPago.getValue().getProveedorId()).isNull();
        assertThat(ordenPago.getValue().getNombreBeneficiario()).isEqualTo("Smoke M4 OP directa");
        assertThat(ordenPago.getValue().getComprobanteId()).isEqualTo(6);
        assertThat(ordenPago.getValue().getPrefijo()).isEqualTo(PREFIJO);
        assertThat(ordenPago.getValue().getNumeroComprobante()).isEqualTo(NUMERO);
        assertThat(ordenPago.getValue().getFechaContable()).isEqualTo(FECHA);
        assertThat(ordenPago.getValue().getOrdenContable()).isEqualTo(3);
        assertThat(ordenPago.getValue().getImporte()).isEqualByComparingTo(TOTAL);
        assertThat(ordenPago.getValue().getCancelado()).isEqualByComparingTo(TOTAL);

        // Tres asientos: encabezado con "Asiento OP Directa" y uno por valor.
        ArgumentCaptor<AsientoEntity> asientos = ArgumentCaptor.forClass(AsientoEntity.class);
        verify(asientoService, org.mockito.Mockito.times(3)).add(asientos.capture());
        assertThat(asientos.getAllValues())
                .extracting(AsientoEntity::getOrden, AsientoEntity::getVinculo)
                .containsExactly(tuple(3, "Asiento OP Directa"),
                        tuple(4, "Pago 0015-00004170"),
                        tuple(5, "Pago 0015-00004170"));

        // Seis líneas de asiento: gasto/obligación y las dos de cada valor, todas con comprobanteId 1.
        ArgumentCaptor<CuentaMovimiento> lineas = ArgumentCaptor.forClass(CuentaMovimiento.class);
        verify(cuentaMovimientoService, org.mockito.Mockito.times(6)).createCuentaMovimiento(lineas.capture());
        assertThat(lineas.getAllValues())
                .extracting(CuentaMovimiento::getOrdenContable, CuentaMovimiento::getNumeroCuenta,
                        CuentaMovimiento::getDebita, CuentaMovimiento::getComprobanteId)
                .containsExactly(tuple(3, CUENTA_GASTO, (byte) 1, 1),
                        tuple(3, CUENTA_OBLIGACION, (byte) 0, 1),
                        tuple(4, CUENTA_OBLIGACION, (byte) 1, 1),
                        tuple(4, CUENTA_IMPUESTO, (byte) 0, 1),
                        tuple(5, CUENTA_OBLIGACION, (byte) 1, 1),
                        tuple(5, CUENTA_BANCO, (byte) 0, 1));
        assertThat(lineas.getAllValues())
                .allSatisfy(linea -> assertThat(linea.getProveedorMovimientoId()).isEqualTo(ORDEN_PAGO_ID));
        assertThat(lineas.getAllValues().get(0).getImporte()).isEqualByComparingTo(TOTAL);
        assertThat(lineas.getAllValues().get(2).getImporte()).isEqualByComparingTo(IMPUESTO);
        assertThat(lineas.getAllValues().get(4).getImporte()).isEqualByComparingTo(TRANSFERENCIA);

        // Valores con el asiento recién reservado y la orden incremental del comprobante.
        ArgumentCaptor<ValorMovimiento> valores = ArgumentCaptor.forClass(ValorMovimiento.class);
        verify(valorMovimientoService, org.mockito.Mockito.times(2)).create(valores.capture());
        assertThat(valores.getAllValues())
                .extracting(ValorMovimiento::valorId, ValorMovimiento::fechaContable, ValorMovimiento::ordenContable,
                        ValorMovimiento::estado)
                .containsExactly(tuple(6, FECHA, 4, "Emitido"), tuple(5, FECHA, 5, "Emitido"));

        ArgumentCaptor<ProveedorValor> asociaciones = ArgumentCaptor.forClass(ProveedorValor.class);
        verify(proveedorValorService, org.mockito.Mockito.times(2)).create(asociaciones.capture());
        assertThat(asociaciones.getAllValues())
                .extracting(ProveedorValor::orden, ProveedorValor::valorMovimientoId)
                .containsExactly(tuple(1, IMPUESTO_ID), tuple(2, TRANSFERENCIA_ID));

        // Sólo la transferencia genera movimiento bancario.
        ArgumentCaptor<BancoMovimiento> banco = ArgumentCaptor.forClass(BancoMovimiento.class);
        verify(bancoMovimientoService).create(banco.capture());
        assertThat(banco.getValue().bancariaId()).isEqualTo(8L);
        assertThat(banco.getValue().orden()).isEqualTo(2L);
        assertThat(banco.getValue().tipo()).isEqualTo("Transferencias");
        assertThat(banco.getValue().importe()).isEqualByComparingTo(TRANSFERENCIA);
        assertThat(banco.getValue().numeroCuenta()).isEqualByComparingTo(CUENTA_BANCO);
        assertThat(banco.getValue().ordenContable()).isEqualTo(5);
        assertThat(banco.getValue().valorMovimientoId()).isEqualTo(TRANSFERENCIA_ID);

        // La orden de pago directa no paga facturas.
        verify(proveedorMovimientoService, never()).updateProveedorMovimiento(any(), anyLong());
    }

    @Test
    void rechazaElAltaSiElPreparadorEncuentraErrores() {
        when(preparador.preparar(any())).thenReturn(preparadoConErrores());

        assertThatThrownBy(() -> useCase.createDirecta(solicitud()))
                .isInstanceOf(OrdenPagoValidacionException.class)
                .hasMessageContaining("ERROR: Diferencia entre VALORES y TOTAL");

        verifyNoInteractions(asientoService, cuentaMovimientoService, reservarOrdenContable,
                proveedorMovimientoService, valorMovimientoService, bancoMovimientoService);
    }

    private OrdenPagoDirectaSolicitud solicitud() {
        return new OrdenPagoDirectaSolicitud("Smoke M4 OP directa", FECHA, FECHA, TOTAL, "Smoke M4",
                (byte) 0, (byte) 0, null, null, false,
                List.of(new OrdenPagoDirectaGasto(CUENTA_GASTO, TOTAL)),
                List.of(new OrdenPagoValor(6, 1L, FECHA, null, IMPUESTO, null, "TITULAR", "20111111112", ""),
                        new OrdenPagoValor(5, 1L, FECHA, null, TRANSFERENCIA, 8L, "TITULAR", "20111111112", "")));
    }

    private OrdenPagoDirectaPreparado preparado() {
        return new OrdenPagoDirectaPreparado(comprobante(), ejercicio(), contexto(),
                TOTAL, TOTAL, PREFIJO, NUMERO, List.of());
    }

    private OrdenPagoDirectaPreparado preparadoConErrores() {
        return new OrdenPagoDirectaPreparado(comprobante(), ejercicio(), contexto(),
                TOTAL, TOTAL, PREFIJO, NUMERO, List.of("ERROR: Diferencia entre VALORES y TOTAL"));
    }

    private OrdenPagoDirectaContexto contexto() {
        return new OrdenPagoDirectaContexto(6, (byte) 1, PREFIJO, NUMERO, FECHA, null, 0, null,
                CUENTA_OBLIGACION, TOTAL,
                List.of(new ContextoGasto(CUENTA_GASTO, TOTAL)),
                List.of(new ContextoValor(null, 6, IMPUESTO, FECHA, null, null, CUENTA_IMPUESTO, 0),
                        new ContextoValor(null, 5, TRANSFERENCIA, FECHA, null, null, CUENTA_BANCO, 1)));
    }

    private ProveedorMovimiento ordenPago() {
        return ProveedorMovimiento.builder()
                .proveedorMovimientoId(ORDEN_PAGO_ID)
                .nombreBeneficiario("Smoke M4 OP directa")
                .comprobanteId(6)
                .prefijo(PREFIJO)
                .numeroComprobante(NUMERO)
                .importe(TOTAL)
                .cancelado(BigDecimal.ZERO)
                .fechaContable(FECHA)
                .ordenContable(3)
                .build();
    }

    private ValorMovimiento valorMovimiento(Long id, Integer valorId, Integer orden, BigDecimal importe,
                                            Long bancariaIdOrigen) {
        return new ValorMovimiento(id, valorId, FECHA, 1L, bancariaIdOrigen, null, importe, "Emitido",
                "TITULAR", "20111111112", 0L, FECHA, orden, "");
    }

    private Comprobante comprobante() {
        return Comprobante.builder()
                .comprobanteId(6)
                .descripcion("Orden de Pago")
                .ordenPago((byte) 1)
                .debita((byte) 1)
                .build();
    }

    private Ejercicio ejercicio() {
        return Ejercicio.builder().ejercicioId(15).bloqueado((byte) 0).build();
    }

    private Valor impuesto() {
        return new Valor(6, "Impuesto a las Ganancias RECTORADO", "Tributo", 0, 0, null, CUENTA_IMPUESTO);
    }

    private Valor transferencia() {
        return new Valor(5, "Transferencia", "Transferencia", 0, 0, null, null);
    }

    private Bancaria bancaria() {
        return Bancaria.builder().bancariaId(8L).numeroCuenta(CUENTA_BANCO).build();
    }
}
