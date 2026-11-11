package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ProveedorMovimientoException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.AnulacionComprobante;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimientoDetalle;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;
import um.tesoreria.core.kotlin.model.EntregaDetalle;
import um.tesoreria.core.kotlin.model.ProveedorPago;
import um.tesoreria.core.kotlin.model.ProveedorValor;
import um.tesoreria.core.kotlin.model.ValorMovimiento;
import um.tesoreria.core.service.EntregaDetalleService;
import um.tesoreria.core.service.ProveedorPagoService;
import um.tesoreria.core.service.ProveedorValorService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProveedorMovimientoDetalleUseCaseImplTest {

    @Mock
    private ProveedorMovimientoRepository repository;
    @Mock
    private ProveedorPagoService proveedorPagoService;
    @Mock
    private ProveedorValorService proveedorValorService;
    @Mock
    private EntregaDetalleService entregaDetalleService;

    private GetProveedorMovimientoDetalleUseCaseImpl useCase() {
        return new GetProveedorMovimientoDetalleUseCaseImpl(repository, proveedorPagoService,
                proveedorValorService, entregaDetalleService);
    }

    private static ProveedorMovimiento movimiento(Byte ordenPago, OffsetDateTime fechaAnulacion) {
        return ProveedorMovimiento.builder()
                .proveedorMovimientoId(1L)
                .proveedorId(7)
                .comprobanteId(1)
                .prefijo(1)
                .numeroComprobante(10L)
                .fechaComprobante(OffsetDateTime.parse("2026-10-02T00:00:00-03:00"))
                .importe(new BigDecimal("100"))
                .cancelado(new BigDecimal("40"))
                .fechaAnulacion(fechaAnulacion)
                .comprobante(Comprobante.builder()
                        .comprobanteId(1)
                        .descripcion(ordenPago != null && ordenPago != 0 ? "Orden de Pago" : "Factura A")
                        .ordenPago(ordenPago)
                        .build())
                .build();
    }

    @Test
    void unaFacturaDevuelveLosPagosAplicados() {
        when(repository.findById(1L)).thenReturn(Optional.of(movimiento((byte) 0, null)));
        when(proveedorPagoService.findAllByFactura(1L)).thenReturn(List.of(new ProveedorPago()));
        when(entregaDetalleService.findAllByProveedorMovimientoId(1L)).thenReturn(List.of());

        ProveedorMovimientoDetalle detalle = useCase().getProveedorMovimientoDetalle(1L);

        assertThat(detalle.getTipo()).isEqualTo(ProveedorMovimientoDetalle.TIPO_FACTURA);
        assertThat(detalle.getPagos()).hasSize(1);
        assertThat(detalle.getValores()).isEmpty();
        assertThat(detalle.getAnulacion().getHabilitada()).isTrue();
        assertThat(detalle.getAnulacion().getMotivos()).isEmpty();
        assertThat(detalle.getAnulacion().getRequierePrivilegioOp()).isFalse();
        assertThat(detalle.getCabecera().getSaldo()).isEqualByComparingTo("60");
        assertThat(detalle.getCabecera().getNumero()).isEqualTo("0001-00000010");
    }

    @Test
    void unaOrdenDePagoDevuelveSusValores() {
        ValorMovimiento valorMovimiento = new ValorMovimiento();
        valorMovimiento.setValorMovimientoId(9L);
        ProveedorValor proveedorValor = new ProveedorValor();
        proveedorValor.setValorMovimiento(valorMovimiento);

        when(repository.findById(1L)).thenReturn(Optional.of(movimiento((byte) 1, null)));
        when(proveedorValorService.findAllByProveedorMovimientoId(1L)).thenReturn(List.of(proveedorValor));
        when(entregaDetalleService.findAllByProveedorMovimientoId(1L)).thenReturn(List.of());

        ProveedorMovimientoDetalle detalle = useCase().getProveedorMovimientoDetalle(1L);

        assertThat(detalle.getTipo()).isEqualTo(ProveedorMovimientoDetalle.TIPO_ORDEN_PAGO);
        assertThat(detalle.getValores()).hasSize(1);
        assertThat(detalle.getPagos()).isEmpty();
        assertThat(detalle.getAnulacion().getRequierePrivilegioOp()).isTrue();
    }

    @Test
    void conCostosAsignadosNoSePuedeAnular() {
        when(repository.findById(1L)).thenReturn(Optional.of(movimiento((byte) 0, null)));
        when(proveedorPagoService.findAllByFactura(1L)).thenReturn(List.of());
        EntregaDetalle entregaDetalle = new EntregaDetalle();
        entregaDetalle.setProveedorMovimientoId(1L);
        when(entregaDetalleService.findAllByProveedorMovimientoId(1L)).thenReturn(List.of(entregaDetalle));

        ProveedorMovimientoDetalle detalle = useCase().getProveedorMovimientoDetalle(1L);

        assertThat(detalle.getAnulacion().getHabilitada()).isFalse();
        assertThat(detalle.getAnulacion().getMotivos())
                .containsExactly(AnulacionComprobante.COSTOS_ASIGNADOS);
    }

    @Test
    void unComprobanteAnuladoNoSePuedeVolverAAnular() {
        when(repository.findById(1L))
                .thenReturn(Optional.of(movimiento((byte) 0, OffsetDateTime.parse("2026-10-05T00:00:00-03:00"))));
        when(proveedorPagoService.findAllByFactura(1L)).thenReturn(List.of());
        when(entregaDetalleService.findAllByProveedorMovimientoId(1L)).thenReturn(List.of());

        ProveedorMovimientoDetalle detalle = useCase().getProveedorMovimientoDetalle(1L);

        assertThat(detalle.getAnulacion().getHabilitada()).isFalse();
        assertThat(detalle.getAnulacion().getMotivos())
                .containsExactly(AnulacionComprobante.YA_ANULADA);
    }

    @Test
    void unComprobanteInexistenteLanzaExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase().getProveedorMovimientoDetalle(99L))
                .isInstanceOf(ProveedorMovimientoException.class);
    }
}
