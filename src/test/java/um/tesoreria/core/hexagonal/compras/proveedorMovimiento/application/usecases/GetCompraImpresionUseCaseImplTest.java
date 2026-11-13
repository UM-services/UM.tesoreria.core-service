package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.entity.ArticuloEntity;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.FindAllByAsientoUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.FindAllByProveedorMovimientoIdUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ComprobanteEsOrdenPagoException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ProveedorMovimientoException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.CompraImpresion;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;
import um.tesoreria.core.kotlin.model.ProveedorArticulo;
import um.tesoreria.core.service.ProveedorArticuloService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCompraImpresionUseCaseImplTest {

    private static final OffsetDateTime FECHA = OffsetDateTime.parse("2026-10-08T00:00:00Z");

    @Mock
    private ProveedorMovimientoRepository repository;
    @Mock
    private ProveedorArticuloService proveedorArticuloService;
    @Mock
    private FindAllByAsientoUseCase findAllByAsientoUseCase;
    @Mock
    private FindAllByProveedorMovimientoIdUseCase findAllByProveedorMovimientoIdUseCase;

    private GetCompraImpresionUseCaseImpl useCase() {
        return new GetCompraImpresionUseCaseImpl(repository, proveedorArticuloService, findAllByAsientoUseCase,
                findAllByProveedorMovimientoIdUseCase);
    }

    private static ProveedorMovimiento comprobante(Long id, Byte ordenPago, String descripcion, String importe,
                                                   OffsetDateTime fechaContable, Integer ordenContable) {
        return ProveedorMovimiento.builder()
                .proveedorMovimientoId(id)
                .proveedorId(7)
                .prefijo(7)
                .numeroComprobante(5682L)
                .fechaComprobante(FECHA)
                .importe(new BigDecimal(importe))
                .concepto("NC por compra de materiales eléctricos")
                .fechaContable(fechaContable)
                .ordenContable(ordenContable)
                .comprobante(Comprobante.builder().comprobanteId(1).descripcion(descripcion).ordenPago(ordenPago).build())
                .proveedor(Proveedor.builder().proveedorId(7).razonSocial("Mas Electro Urquiza S.A.").domicilio("Calle 1")
                        .cuit("30-70988213-5").build())
                .build();
    }

    private static ProveedorArticulo renglon(int orden, String articulo, String concepto, String cantidad,
                                             String unitario, String finalPrecio) {
        ProveedorArticulo proveedorArticulo = new ProveedorArticulo();
        proveedorArticulo.setOrden(orden);
        proveedorArticulo.setArticulo(ArticuloEntity.builder().nombre(articulo).build());
        proveedorArticulo.setConcepto(concepto);
        proveedorArticulo.setCantidad(new BigDecimal(cantidad));
        proveedorArticulo.setPrecioUnitario(new BigDecimal(unitario));
        proveedorArticulo.setPrecioFinal(new BigDecimal(finalPrecio));
        return proveedorArticulo;
    }

    private static CuentaMovimiento linea(int item, long cuenta, String nombre, int debita, String importe) {
        return CuentaMovimiento.builder()
                .item(item)
                .numeroCuenta(new BigDecimal(cuenta))
                .debita((byte) debita)
                .importe(new BigDecimal(importe))
                .cuenta(Cuenta.builder().numeroCuenta(new BigDecimal(cuenta)).nombre(nombre).build())
                .build();
    }

    @Test
    void unaNotaDeCreditoDevuelveDetalleConSignoYAsientoConElDebePrimero() {
        when(repository.findById(20L)).thenReturn(Optional.of(
                comprobante(20L, (byte) 0, "N. Credito B", "-344825.51", FECHA, 60)));
        when(proveedorArticuloService.findAllByProveedorMovimientoId(20L, false)).thenReturn(List.of(
                renglon(1, "Mantenimiento Bienes de Uso", "", "1.00", "344825.51", "-344825.51")));
        // las líneas ligadas al comprobante (movcon.proveedormovimiento_id), con el haber antes que el debe
        when(findAllByProveedorMovimientoIdUseCase.findAllByProveedorMovimientoId(20L)).thenReturn(List.of(
                linea(1, 40102980005L, "Mantenimiento Bienes de Uso ***", 0, "344825.51"),
                linea(2, 40102040005L, "Mantenimiento Bienes de Uso FI", 0, "344825.51"),
                linea(3, 20101090099L, "Obligaciones a Pagar", 1, "344825.51"),
                linea(4, 40102980005L, "Mantenimiento Bienes de Uso ***", 1, "344825.51")));

        CompraImpresion impresion = useCase().getCompraImpresion(20L);

        assertThat(impresion.getComprobanteDescripcion()).isEqualTo("N. Credito B");
        assertThat(impresion.getNumero()).isEqualTo("0007-00005682");
        assertThat(impresion.getRazonSocial()).isEqualTo("Mas Electro Urquiza S.A.");
        assertThat(impresion.getImporte()).isEqualByComparingTo("-344825.51");
        assertThat(impresion.getDetalle()).singleElement().satisfies(d -> {
            assertThat(d.articulo()).isEqualTo("Mantenimiento Bienes de Uso");
            assertThat(d.precioUnitario()).isEqualByComparingTo("344825.51");
            assertThat(d.precioFinal()).isEqualByComparingTo("-344825.51");
        });
        assertThat(impresion.getOrdenContable()).isEqualTo(60);
        assertThat(impresion.getAsiento()).extracting(CompraImpresion.AsientoLinea::numeroCuenta)
                .extracting(BigDecimal::longValue)
                .containsExactly(20101090099L, 40102980005L, 40102980005L, 40102040005L);
        assertThat(impresion.getAsiento().get(0).debe()).isEqualByComparingTo("344825.51");
        assertThat(impresion.getAsiento().get(2).haber()).isEqualByComparingTo("344825.51");
        assertThat(impresion.getTotalDebe()).isEqualByComparingTo("689651.02");
        assertThat(impresion.getTotalHaber()).isEqualByComparingTo("689651.02");
    }

    @Test
    void unaFacturaSinAsientoDevuelveListaVacia() {
        when(repository.findById(21L)).thenReturn(Optional.of(
                comprobante(21L, (byte) 0, "Factura B", "364893.02", null, null)));
        when(proveedorArticuloService.findAllByProveedorMovimientoId(21L, false)).thenReturn(List.of());
        when(findAllByProveedorMovimientoIdUseCase.findAllByProveedorMovimientoId(21L)).thenReturn(List.of());

        CompraImpresion impresion = useCase().getCompraImpresion(21L);

        assertThat(impresion.getDetalle()).isEmpty();
        assertThat(impresion.getAsiento()).isEmpty();
        assertThat(impresion.getTotalDebe()).isEqualByComparingTo("0");
        assertThat(impresion.getTotalHaber()).isEqualByComparingTo("0");
    }

    @Test
    void lasLineasDeVariosAsientosLigadosAlComprobanteSeImprimenTodas() {
        when(repository.findById(22L)).thenReturn(Optional.of(
                comprobante(22L, (byte) 0, "Factura B", "364893.02", FECHA, 74)));
        when(proveedorArticuloService.findAllByProveedorMovimientoId(22L, false)).thenReturn(List.of());
        // como la factura 0007-00118482 de VB6: 4 líneas, dos en el debe y dos en el haber
        when(findAllByProveedorMovimientoIdUseCase.findAllByProveedorMovimientoId(22L)).thenReturn(List.of(
                linea(1, 20101090099L, "Obligaciones a Pagar", 0, "364893.02"),
                linea(2, 40102980005L, "Mantenimiento Bienes de Uso ***", 0, "364893.02"),
                linea(3, 40102980005L, "Mantenimiento Bienes de Uso ***", 1, "364893.02"),
                linea(4, 40102010005L, "Mantenimiento Bienes de Uso RECT", 1, "364893.02")));

        CompraImpresion impresion = useCase().getCompraImpresion(22L);

        assertThat(impresion.getAsiento()).hasSize(4);
        assertThat(impresion.getTotalDebe()).isEqualByComparingTo("729786.04");
        assertThat(impresion.getTotalHaber()).isEqualByComparingTo("729786.04");
    }

    @Test
    void sinLineasLigadasAlComprobanteSeUsaLaFechaYOrdenContable() {
        when(repository.findById(23L)).thenReturn(Optional.of(
                comprobante(23L, (byte) 0, "Factura B", "100", FECHA, 5)));
        when(proveedorArticuloService.findAllByProveedorMovimientoId(23L, false)).thenReturn(List.of());
        when(findAllByProveedorMovimientoIdUseCase.findAllByProveedorMovimientoId(23L)).thenReturn(List.of());
        when(findAllByAsientoUseCase.findAllByAsiento(FECHA, 5, 0, 2)).thenReturn(List.of(
                linea(1, 11111L, "Caja", 1, "100"), linea(2, 22222L, "Proveedores", 0, "100")));

        assertThat(useCase().getCompraImpresion(23L).getAsiento()).hasSize(2);
    }

    @Test
    void unaOrdenDePagoSeRechaza() {
        when(repository.findById(10L)).thenReturn(Optional.of(
                comprobante(10L, (byte) 1, "Orden de Pago", "1000", FECHA, 5)));

        assertThatThrownBy(() -> useCase().getCompraImpresion(10L))
                .isInstanceOf(ComprobanteEsOrdenPagoException.class);
    }

    @Test
    void unComprobanteInexistenteSeRechaza() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase().getCompraImpresion(99L))
                .isInstanceOf(ProveedorMovimientoException.class);
    }
}