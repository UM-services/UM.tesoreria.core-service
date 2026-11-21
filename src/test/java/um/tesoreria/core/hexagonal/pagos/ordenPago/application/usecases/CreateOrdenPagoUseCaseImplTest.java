package um.tesoreria.core.hexagonal.pagos.ordenPago.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ReservarOrdenContableUseCase;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.GetBancariaByIdUseCase;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.application.service.BancoMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.service.EjercicioService;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.pagos.entrega.application.service.EntregaService;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.exception.OrdenPagoValidacionException;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.model.OrdenPagoPreparado;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.service.OrdenPagoPreparador;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ContextoArticulo;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ContextoFactura;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ContextoValor;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoContexto;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoFactura;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoResultado;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoSolicitud;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoValor;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.application.service.ProveedorPagoService;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.model.ProveedorPago;
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
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Paridad con {@code clsCtlCompras.savePago}: una reserva de asiento por valor, el comprobante de la
 * OP en las líneas de valor y el registro de pagos/cancelados por factura.
 */
@ExtendWith(MockitoExtension.class)
class CreateOrdenPagoUseCaseImplTest {

    private static final OffsetDateTime FECHA_OP = OffsetDateTime.of(2026, 11, 13, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime FECHA_FACTURA = OffsetDateTime.of(2026, 11, 10, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final BigDecimal TOTAL = new BigDecimal("555854.06");
    private static final BigDecimal IMPUESTO = new BigDecimal("4707.67");
    private static final BigDecimal TRANSFERENCIA = new BigDecimal("551146.39");
    private static final BigDecimal CUENTA_PROVEEDOR = new BigDecimal("20101090099");
    private static final BigDecimal CUENTA_ARTICULO = new BigDecimal("40102980004");
    private static final BigDecimal CUENTA_IMPUESTO = new BigDecimal("20101010009");
    private static final BigDecimal CUENTA_BANCO = new BigDecimal("10102040001");
    private static final Long OP_ID = 104249L;
    private static final Long FACTURA_ID = 104222L;

    @Mock
    private OrdenPagoPreparador preparador;
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
    private ProveedorPagoService proveedorPagoService;
    @Mock
    private BancoMovimientoService bancoMovimientoService;
    @Mock
    private ValorService valorService;
    @Mock
    private GetBancariaByIdUseCase getBancariaByIdUseCase;
    @Mock
    private EjercicioService ejercicioService;
    @Mock
    private EntregaService entregaService;

    private CreateOrdenPagoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateOrdenPagoUseCaseImpl(preparador, proveedorMovimientoService, asientoService,
                cuentaMovimientoService, reservarOrdenContable, valorMovimientoService, proveedorValorService,
                proveedorPagoService, bancoMovimientoService, valorService, getBancariaByIdUseCase, ejercicioService,
                entregaService);
    }

    @Test
    void registraParidadSavePagoConUnAsientoPorValor() {
        OrdenPagoSolicitud solicitud = solicitud();
        preparar(solicitud, facturaContabilizada());

        when(proveedorMovimientoService.createProveedorMovimiento(any())).thenAnswer(invocation -> {
            ProveedorMovimiento ordenPago = invocation.getArgument(0);
            ordenPago.setProveedorMovimientoId(OP_ID);
            return ordenPago;
        });
        when(proveedorMovimientoService.updateProveedorMovimiento(any(), anyLong()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(reservarOrdenContable.reservar(FECHA_OP)).thenReturn(70, 71);
        when(ejercicioService.findByFecha(any(LocalDate.class))).thenReturn(Optional.of(ejercicio()));
        when(valorService.getValorById(6)).thenReturn(impuesto());
        when(valorService.getValorById(5)).thenReturn(transferencia());
        when(getBancariaByIdUseCase.getBancariaById(8L)).thenReturn(bancaria());
        when(bancoMovimientoService.nextOrden(8L, FECHA_OP)).thenReturn(1L);
        when(proveedorPagoService.existsTrazaContableByFactura(FACTURA_ID)).thenReturn(false);
        AtomicLong valorId = new AtomicLong(84023L);
        when(valorMovimientoService.create(any())).thenAnswer(invocation -> {
            ValorMovimiento valorMovimiento = invocation.getArgument(0);
            return new ValorMovimiento(valorId.getAndIncrement(), valorMovimiento.valorId(),
                    valorMovimiento.fechaEmision(), valorMovimiento.numero(), valorMovimiento.bancariaIdOrigen(),
                    valorMovimiento.fechaEfectivizacion(), valorMovimiento.importe(), valorMovimiento.estado(),
                    valorMovimiento.nombreTitular(), valorMovimiento.cuitTitular(), valorMovimiento.reemplazo(),
                    valorMovimiento.fechaContable(), valorMovimiento.ordenContable(), valorMovimiento.letras());
        });

        OrdenPagoResultado resultado = useCase.create(solicitud);

        assertThat(resultado.proveedorMovimientoId()).isEqualTo(OP_ID);
        assertThat(resultado.prefijo()).isEqualTo(15);
        assertThat(resultado.numeroComprobante()).isEqualTo(4086L);
        assertThat(resultado.importe()).isEqualByComparingTo(TOTAL);
        assertThat(resultado.cancelado()).isEqualByComparingTo(TOTAL);
        assertThat(resultado.valorMovimientoIds()).containsExactly(84023L, 84024L);
        assertThat(resultado.asientos()).hasSize(2);

        verify(reservarOrdenContable, times(2)).reservar(FECHA_OP);

        ArgumentCaptor<AsientoEntity> asientos = ArgumentCaptor.forClass(AsientoEntity.class);
        verify(asientoService, times(2)).add(asientos.capture());
        assertThat(asientos.getAllValues()).extracting(AsientoEntity::getOrden).containsExactly(70, 71);
        assertThat(asientos.getAllValues()).allSatisfy(asiento -> {
            assertThat(asiento.getFecha()).isEqualTo(FECHA_OP);
            assertThat(asiento.getVinculo()).isEqualTo("Pago 0015-00004086 - DOLTIN S.R.L. (30-12345678-9)");
        });

        ArgumentCaptor<CuentaMovimiento> lineas = ArgumentCaptor.forClass(CuentaMovimiento.class);
        verify(cuentaMovimientoService, times(4)).createCuentaMovimiento(lineas.capture());
        assertThat(lineas.getAllValues()).extracting(CuentaMovimiento::getComprobanteId).containsOnly(6);
        assertThat(lineas.getAllValues()).extracting(CuentaMovimiento::getProveedorMovimientoId).containsOnly(OP_ID);
        assertThat(lineas.getAllValues()).extracting(CuentaMovimiento::getItem).containsExactly(1, 2, 1, 2);
        assertThat(lineas.getAllValues().getFirst().getNumeroCuenta()).isEqualByComparingTo(CUENTA_PROVEEDOR);
        assertThat(lineas.getAllValues().getFirst().getDebita()).isEqualTo((byte) 1);
        assertThat(lineas.getAllValues().get(1).getNumeroCuenta()).isEqualByComparingTo(CUENTA_IMPUESTO);
        assertThat(lineas.getAllValues().get(1).getDebita()).isEqualTo((byte) 0);
        assertThat(lineas.getAllValues().get(3).getNumeroCuenta()).isEqualByComparingTo(CUENTA_BANCO);

        ArgumentCaptor<ValorMovimiento> valores = ArgumentCaptor.forClass(ValorMovimiento.class);
        verify(valorMovimientoService, times(2)).create(valores.capture());
        assertThat(valores.getAllValues()).extracting(ValorMovimiento::estado).containsOnly("Emitido");
        assertThat(valores.getAllValues()).extracting(ValorMovimiento::fechaContable).containsOnly(FECHA_OP);
        assertThat(valores.getAllValues()).extracting(ValorMovimiento::ordenContable).containsExactly(70, 71);

        ArgumentCaptor<ProveedorValor> proveedorValores = ArgumentCaptor.forClass(ProveedorValor.class);
        verify(proveedorValorService, times(2)).create(proveedorValores.capture());
        assertThat(proveedorValores.getAllValues()).extracting(ProveedorValor::orden).containsExactly(1, 2);

        ArgumentCaptor<ProveedorPago> pagos = ArgumentCaptor.forClass(ProveedorPago.class);
        verify(proveedorPagoService).create(pagos.capture());
        ProveedorPago pago = pagos.getValue();
        assertThat(pago.proveedorMovimientoIdPago()).isEqualTo(OP_ID);
        assertThat(pago.proveedorMovimientoIdFactura()).isEqualTo(FACTURA_ID);
        assertThat(pago.importeAplicado()).isEqualByComparingTo(TOTAL);
        assertThat(pago.fechaPago()).isEqualTo(FECHA_OP);
        assertThat(pago.trazaContable()).isEqualTo((byte) 1);

        verify(proveedorMovimientoService, times(2)).updateProveedorMovimiento(any(), anyLong());
        verify(entregaService, never()).findAsignacionesByProveedorMovimientoId(any());
    }

    @Test
    void completaElAsientoDeLaFacturaCuandoNoTieneOrdenContable() {
        OrdenPagoSolicitud solicitud = solicitud();
        preparar(solicitud, facturaSinAsiento());

        when(proveedorMovimientoService.createProveedorMovimiento(any())).thenAnswer(invocation -> {
            ProveedorMovimiento ordenPago = invocation.getArgument(0);
            ordenPago.setProveedorMovimientoId(OP_ID);
            return ordenPago;
        });
        when(proveedorMovimientoService.updateProveedorMovimiento(any(), anyLong()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(reservarOrdenContable.reservar(FECHA_OP)).thenReturn(70, 71, 72);
        when(ejercicioService.findByFecha(any(LocalDate.class))).thenReturn(Optional.of(ejercicio()));
        when(valorService.getValorById(6)).thenReturn(impuesto());
        when(valorService.getValorById(5)).thenReturn(transferencia());
        when(getBancariaByIdUseCase.getBancariaById(8L)).thenReturn(bancaria());
        when(bancoMovimientoService.nextOrden(8L, FECHA_OP)).thenReturn(1L);
        when(proveedorPagoService.existsTrazaContableByFactura(FACTURA_ID)).thenReturn(true);
        when(valorMovimientoService.create(any())).thenAnswer(invocation -> invocation.getArgument(0));

        OrdenPagoResultado resultado = useCase.create(solicitud);

        assertThat(resultado.asientos()).hasSize(3);
        verify(reservarOrdenContable, times(3)).reservar(FECHA_OP);

        ArgumentCaptor<ProveedorMovimiento> actualizaciones = ArgumentCaptor.forClass(ProveedorMovimiento.class);
        verify(proveedorMovimientoService, atLeastFacturaYOrdenPago()).updateProveedorMovimiento(actualizaciones.capture(), anyLong());
        ProveedorMovimiento factura = actualizaciones.getAllValues().stream()
                .filter(movimiento -> FACTURA_ID.equals(movimiento.getProveedorMovimientoId()))
                .findFirst()
                .orElseThrow();
        assertThat(factura.getFechaContable()).isEqualTo(FECHA_OP);
        // los dos primeros órdenes los toman los valores; la factura recibe el tercero
        assertThat(factura.getOrdenContable()).isEqualTo(72);

        ArgumentCaptor<CuentaMovimiento> lineas = ArgumentCaptor.forClass(CuentaMovimiento.class);
        verify(cuentaMovimientoService, times(6)).createCuentaMovimiento(lineas.capture());
        assertThat(lineas.getAllValues().stream()
                .filter(linea -> Integer.valueOf(4).equals(linea.getComprobanteId()))
                .toList()).hasSize(2);
        assertThat(lineas.getAllValues().stream()
                .filter(linea -> FACTURA_ID.equals(linea.getProveedorMovimientoId()))
                .count()).isEqualTo(2);

        ArgumentCaptor<ProveedorPago> pagos = ArgumentCaptor.forClass(ProveedorPago.class);
        verify(proveedorPagoService).create(pagos.capture());
        assertThat(pagos.getValue().trazaContable()).isEqualTo((byte) 0);
    }

    @Test
    void rechazaElAltaSiElPreparadorEncuentraErrores() {
        OrdenPagoSolicitud solicitud = solicitud();
        when(preparador.preparar(solicitud)).thenReturn(new OrdenPagoPreparado(
                proveedor(), comprobante(), ejercicio(), List.of(), List.of(), contexto(facturaContabilizada()),
                TOTAL, TOTAL, 15, 4086L, List.of("ERROR: Asiento NO CUADRA")));

        assertThatThrownBy(() -> useCase.create(solicitud))
                .isInstanceOf(OrdenPagoValidacionException.class)
                .hasMessageContaining("ERROR: Asiento NO CUADRA");

        verify(proveedorMovimientoService, never()).createProveedorMovimiento(any());
        verify(reservarOrdenContable, never()).reservar(any());
    }

    private org.mockito.verification.VerificationMode atLeastFacturaYOrdenPago() {
        // dos actualizaciones de la factura (cancelado y traza contable) y una de la orden de pago
        return times(3);
    }

    private void preparar(OrdenPagoSolicitud solicitud, ContextoFactura factura) {
        when(preparador.preparar(solicitud)).thenReturn(new OrdenPagoPreparado(proveedor(), comprobante(), ejercicio(),
                List.of(proveedorMovimiento(factura.ordenContable())), List.of(new OrdenPagoFactura(FACTURA_ID, TOTAL)),
                contexto(factura), TOTAL, TOTAL, 15, 4086L, List.of()));
    }

    private OrdenPagoSolicitud solicitud() {
        return new OrdenPagoSolicitud(4484, 6, FECHA_OP, FECHA_OP, TOTAL,
                "Compra de insumos de limpieza - SRC.", (byte) 0, (byte) 0, null, null, false, null, null,
                List.of(new OrdenPagoFactura(FACTURA_ID, TOTAL)),
                List.of(new OrdenPagoValor(6, 2026111304L, FECHA_OP, null, IMPUESTO, null, "TITULAR", "20111111112", ""),
                        new OrdenPagoValor(5, 69557253L, FECHA_OP, null, TRANSFERENCIA, 8L, "TITULAR", "20111111112", "")));
    }

    private ContextoFactura facturaContabilizada() {
        return factura(FECHA_FACTURA, 20);
    }

    private ContextoFactura facturaSinAsiento() {
        return factura(null, 0);
    }

    private ContextoFactura factura(OffsetDateTime fechaContable, Integer ordenContable) {
        return new ContextoFactura(FACTURA_ID, 4, (byte) 0, 4484, TOTAL, CUENTA_PROVEEDOR, fechaContable,
                ordenContable, List.of(new ContextoArticulo(CUENTA_ARTICULO, TOTAL)));
    }

    private OrdenPagoContexto contexto(ContextoFactura factura) {
        return new OrdenPagoContexto(4484, 6, (byte) 1, CUENTA_PROVEEDOR, 15, 4086L, FECHA_OP, null,
                List.of(factura), List.of(),
                List.of(new ContextoValor(null, 6, IMPUESTO, FECHA_OP, null, null, CUENTA_IMPUESTO, 0),
                        new ContextoValor(null, 5, TRANSFERENCIA, FECHA_OP, null, null, CUENTA_BANCO, 1)));
    }

    private Proveedor proveedor() {
        return Proveedor.builder()
                .proveedorId(4484)
                .razonSocial("DOLTIN S.R.L.")
                .cuit("30-12345678-9")
                .numeroCuenta(CUENTA_PROVEEDOR)
                .build();
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
        return Ejercicio.builder()
                .ejercicioId(15)
                .bloqueado((byte) 0)
                .build();
    }

    private ProveedorMovimiento proveedorMovimiento(Integer ordenContable) {
        return ProveedorMovimiento.builder()
                .proveedorMovimientoId(FACTURA_ID)
                .proveedorId(4484)
                .comprobanteId(4)
                .importe(TOTAL)
                .cancelado(BigDecimal.ZERO)
                .fechaContable(ordenContable != null && ordenContable > 0 ? FECHA_FACTURA : null)
                .ordenContable(ordenContable)
                .build();
    }

    private Valor impuesto() {
        return new Valor(6, "Impuesto a las Ganancias RECTORADO", "Tributo", 0, 0, null, CUENTA_IMPUESTO);
    }

    private Valor transferencia() {
        return new Valor(5, "Transferencia", "Transferencia", 0, 0, null, null);
    }

    private Bancaria bancaria() {
        return Bancaria.builder()
                .bancariaId(8L)
                .numeroCuenta(CUENTA_BANCO)
                .build();
    }
}
