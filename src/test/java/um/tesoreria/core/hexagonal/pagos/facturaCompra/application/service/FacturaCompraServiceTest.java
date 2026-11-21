package um.tesoreria.core.hexagonal.pagos.facturaCompra.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.articulo.application.service.ArticuloService;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.proveedor.application.service.ProveedorService;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.comprobante.application.service.ComprobanteService;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableResultado;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ReservarOrdenContableUseCase;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.application.exception.FacturaCompraException;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model.FacturaCompra;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model.FacturaCompraArticulo;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model.FacturaCompraDetalle;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model.FacturaCompraResultado;
import um.tesoreria.core.kotlin.model.ProveedorArticulo;
import um.tesoreria.core.service.ProveedorArticuloService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacturaCompraServiceTest {

    private static final OffsetDateTime FECHA =
            OffsetDateTime.of(2026, 11, 19, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final Long ARTICULO_ID = 10L;

    @Mock ProveedorMovimientoService proveedorMovimientoService;
    @Mock ProveedorArticuloService proveedorArticuloService;
    @Mock ProveedorService proveedorService;
    @Mock ComprobanteService comprobanteService;
    @Mock ArticuloService articuloService;
    @Mock AsientoService asientoService;
    @Mock CuentaMovimientoService cuentaMovimientoService;
    @Mock CuentaService cuentaService;
    @Mock ReservarOrdenContableUseCase reservarOrdenContable;

    FacturaCompraService service;

    @BeforeEach
    void preparar() {
        service = new FacturaCompraService(proveedorMovimientoService, proveedorArticuloService,
                proveedorService, comprobanteService, articuloService, asientoService,
                cuentaMovimientoService, cuentaService, reservarOrdenContable);
        lenient().when(cuentaService.findAllByNumeroCuentaIn(any())).thenReturn(List.of());
        lenient().when(cuentaMovimientoService.createCuentaMovimiento(any()))
                .thenAnswer(invocacion -> invocacion.getArgument(0));
        lenient().when(proveedorArticuloService.add(any()))
                .thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    private Proveedor proveedor() {
        return Proveedor.builder()
                .proveedorId(1)
                .razonSocial("Proveedor Test")
                .numeroCuenta(new BigDecimal("20101090099"))
                .build();
    }

    private Comprobante comprobante(byte debita) {
        return Comprobante.builder()
                .comprobanteId(4)
                .descripcion("Factura B")
                .debita(debita)
                .build();
    }

    private Articulo articulo() {
        return Articulo.builder()
                .articuloId(ARTICULO_ID)
                .nombre("Artículo Test")
                .numeroCuenta(new BigDecimal("30101020002"))
                .directo((byte) 0)
                .build();
    }

    private FacturaCompra factura(BigDecimal total, BigDecimal precioFinal) {
        return new FacturaCompra(1, 4, 1, 1L, FECHA, FECHA,
                total, total, "Compra de insumos", null,
                List.of(new FacturaCompraArticulo(ARTICULO_ID, "Insumos",
                        BigDecimal.ONE, precioFinal, precioFinal)));
    }

    private void prepararAlta() {
        when(proveedorService.getByProveedorId(1)).thenReturn(Optional.of(proveedor()));
        when(comprobanteService.findByComprobanteId(4)).thenReturn(comprobante((byte) 1));
        when(proveedorMovimientoService.findByComprobante(1, 4, 1, 1L)).thenReturn(Optional.empty());
        when(reservarOrdenContable.reservar(FECHA)).thenReturn(10);
        when(proveedorMovimientoService.createProveedorMovimiento(any())).thenAnswer(invocacion -> {
            ProveedorMovimiento movimiento = invocacion.getArgument(0);
            movimiento.setProveedorMovimientoId(100L);
            return movimiento;
        });
        when(articuloService.getArticuloById(ARTICULO_ID)).thenReturn(Optional.of(articulo()));
    }

    @Test
    void registraFacturaConAsientoYArticulos() {
        prepararAlta();

        FacturaCompraResultado resultado = service.registrar(factura(new BigDecimal("100.00"),
                new BigDecimal("100.00")));

        assertThat(resultado.proveedorMovimientoId()).isEqualTo(100L);
        assertThat(resultado.ordenContable()).isEqualTo(10);
        assertThat(resultado.vinculo()).isEqualTo("Factura de Compra 0001-00000001");
        assertThat(resultado.totalDebe()).isEqualByComparingTo("100.00");
        assertThat(resultado.totalHaber()).isEqualByComparingTo("100.00");
        assertThat(resultado.cuadra()).isTrue();
        assertThat(resultado.lineas()).hasSize(2);
        ArgumentCaptor<AsientoEntity> cabecera = ArgumentCaptor.forClass(AsientoEntity.class);
        verify(asientoService).add(cabecera.capture());
        assertThat(cabecera.getValue().getVinculo()).isEqualTo("Factura de Compra 0001-00000001");

        ArgumentCaptor<ProveedorArticulo> articulo = ArgumentCaptor.forClass(ProveedorArticulo.class);
        verify(proveedorArticuloService).add(articulo.capture());
        assertThat(articulo.getValue().getProveedorMovimientoId()).isEqualTo(100L);
        assertThat(articulo.getValue().getOrden()).isEqualTo(1);
        assertThat(articulo.getValue().getPrecioUnitario()).isEqualByComparingTo("-100.00");
        assertThat(articulo.getValue().getPrecioFinal()).isEqualByComparingTo("-100.00");
    }

    @Test
    void rechazaSiLaSumaDeArticulosNoCoincideConElTotal() {
        assertThatThrownBy(() -> service.registrar(factura(new BigDecimal("100.00"),
                new BigDecimal("90.00"))))
                .isInstanceOf(FacturaCompraException.class)
                .hasMessageContaining("no coincide");

        verify(proveedorMovimientoService, never()).createProveedorMovimiento(any());
        verify(asientoService, never()).add(any());
    }

    @Test
    void rechazaFacturaDuplicada() {
        when(proveedorService.getByProveedorId(1)).thenReturn(Optional.of(proveedor()));
        when(comprobanteService.findByComprobanteId(4)).thenReturn(comprobante((byte) 1));
        when(proveedorMovimientoService.findByComprobante(1, 4, 1, 1L))
                .thenReturn(Optional.of(ProveedorMovimiento.builder().proveedorMovimientoId(99L).build()));

        assertThatThrownBy(() -> service.registrar(factura(new BigDecimal("100.00"),
                new BigDecimal("100.00"))))
                .isInstanceOf(FacturaCompraException.class)
                .hasMessageContaining("ya existe");

        verify(asientoService, never()).add(any());
    }

    @Test
    void consultaDetalleConArticulosYLineas() {
        ProveedorMovimiento movimiento = ProveedorMovimiento.builder()
                .proveedorMovimientoId(100L)
                .proveedorId(1)
                .nombreBeneficiario("Proveedor Test")
                .comprobanteId(4)
                .prefijo(1)
                .numeroComprobante(1L)
                .fechaComprobante(FECHA)
                .fechaVencimiento(FECHA)
                .neto(new BigDecimal("-100.00"))
                .importe(new BigDecimal("-100.00"))
                .concepto("Compra")
                .fechaContable(FECHA)
                .ordenContable(10)
                .build();
        when(proveedorMovimientoService.findByProveedorMovimientoId(100L)).thenReturn(movimiento);

        ProveedorArticulo articulo = new ProveedorArticulo();
        articulo.setArticuloId(ARTICULO_ID);
        articulo.setCantidad(BigDecimal.ONE);
        articulo.setConcepto("Insumos");
        articulo.setPrecioUnitario(new BigDecimal("-100.00"));
        articulo.setPrecioFinal(new BigDecimal("-100.00"));
        articulo.setEntregado(BigDecimal.ZERO);
        articulo.setAsignado(BigDecimal.ZERO);
        when(proveedorArticuloService.findAllByProveedorMovimientoId(100L, false))
                .thenReturn(List.of(articulo));
        when(comprobanteService.findByComprobanteId(4)).thenReturn(comprobante((byte) 1));
        when(articuloService.getArticuloById(ARTICULO_ID)).thenReturn(Optional.of(articulo()));
        when(cuentaMovimientoService.findAllByAsiento(FECHA, 10, 0, 2)).thenReturn(List.of(
                CuentaMovimiento.builder().numeroCuenta(new BigDecimal("20101090099"))
                        .comprobanteId(4).debita((byte) 1).importe(new BigDecimal("100.00")).build(),
                CuentaMovimiento.builder().numeroCuenta(new BigDecimal("30101020002"))
                        .comprobanteId(4).debita((byte) 0).importe(new BigDecimal("100.00")).build()));

        FacturaCompraDetalle detalle = service.consultar(100L);

        assertThat(detalle.proveedorMovimientoId()).isEqualTo(100L);
        assertThat(detalle.comprobanteDescripcion()).isEqualTo("Factura B");
        assertThat(detalle.articulos()).hasSize(1);
        assertThat(detalle.articulos().get(0).articuloNombre()).isEqualTo("Artículo Test");
        assertThat(detalle.lineas()).hasSize(2);
    }

    @Test
    void existeDelegaEnLaClaveDeUnicidad() {
        when(proveedorMovimientoService.findByComprobante(1, 4, 1, 1L))
                .thenReturn(Optional.of(ProveedorMovimiento.builder().proveedorMovimientoId(99L).build()));

        assertThat(service.existe(1, 4, 1, 1L)).isTrue();
    }

}
