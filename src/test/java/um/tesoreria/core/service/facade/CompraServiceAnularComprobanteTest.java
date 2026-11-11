package um.tesoreria.core.service.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.exception.ComprobanteAnulacionException;
import um.tesoreria.core.exception.PermisoInsuficienteException;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.AnulacionComprobante;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.model.PermisoEfectivo;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.ports.in.GetPermisosEfectivosUseCase;
import um.tesoreria.core.kotlin.model.EntregaDetalle;
import um.tesoreria.core.service.EntregaDetalleService;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Reglas de negocio del comando de anulación (R6, R7 y R10) sin base de datos:
 * la cascada real se reemplaza por un espía sobre {@code deleteComprobante}.
 */
@ExtendWith(MockitoExtension.class)
class CompraServiceAnularComprobanteTest {

    @Mock
    private ProveedorMovimientoService proveedorMovimientoService;
    @Mock
    private EntregaDetalleService entregaDetalleService;
    @Mock
    private GetPermisosEfectivosUseCase getPermisosEfectivosUseCase;
    @Spy
    @InjectMocks
    private CompraService compra;

    private static ProveedorMovimiento movimiento(Byte ordenPago, OffsetDateTime fechaAnulacion) {
        return ProveedorMovimiento.builder()
                .proveedorMovimientoId(123L)
                .fechaAnulacion(fechaAnulacion)
                .comprobante(Comprobante.builder()
                        .comprobanteId(1)
                        .descripcion(ordenPago != null && ordenPago != 0 ? "Orden de Pago" : "Factura A")
                        .ordenPago(ordenPago)
                        .build())
                .build();
    }

    private void comprobante(ProveedorMovimiento movimiento) {
        when(proveedorMovimientoService.findByProveedorMovimientoId(123L)).thenReturn(movimiento);
    }

    private void permisos(String... claves) {
        when(getPermisosEfectivosUseCase.getPermisosEfectivos(5L)).thenReturn(
                PermisoEfectivo.builder().userId(5L).permisos(List.of(claves)).build());
    }

    @Test
    void unComprobanteYaAnuladoNoSeBorra() {
        comprobante(movimiento((byte) 0, OffsetDateTime.parse("2026-10-05T00:00:00Z")));
        when(entregaDetalleService.findAllByProveedorMovimientoId(123L)).thenReturn(List.of());

        assertThatThrownBy(() -> compra.anularComprobante(123L, 5L))
                .isInstanceOfSatisfying(ComprobanteAnulacionException.class,
                        e -> assertThat(e.getMotivos()).containsExactly(AnulacionComprobante.YA_ANULADA));
        verify(compra, never()).deleteComprobante(anyLong());
    }

    @Test
    void conCostosAsignadosNoSeBorra() {
        comprobante(movimiento((byte) 0, null));
        EntregaDetalle detalle = new EntregaDetalle();
        detalle.setProveedorMovimientoId(123L);
        when(entregaDetalleService.findAllByProveedorMovimientoId(123L)).thenReturn(List.of(detalle));

        assertThatThrownBy(() -> compra.anularComprobante(123L, 5L))
                .isInstanceOfSatisfying(ComprobanteAnulacionException.class,
                        e -> assertThat(e.getMotivos()).containsExactly(AnulacionComprobante.COSTOS_ASIGNADOS));
        verify(compra, never()).deleteComprobante(anyLong());
    }

    @Test
    void unaOrdenDePagoSinPrivilegioNoSeBorra() {
        comprobante(movimiento((byte) 1, null));
        when(entregaDetalleService.findAllByProveedorMovimientoId(123L)).thenReturn(List.of());
        permisos("pagos.comprobantes.anular");

        assertThatThrownBy(() -> compra.anularComprobante(123L, 5L))
                .isInstanceOf(PermisoInsuficienteException.class);
        verify(compra, never()).deleteComprobante(anyLong());
    }

    @Test
    void unaOrdenDePagoConPrivilegioSeBorra() {
        comprobante(movimiento((byte) 1, null));
        when(entregaDetalleService.findAllByProveedorMovimientoId(123L)).thenReturn(List.of());
        permisos("pagos.comprobantes.anular", CompraService.PERMISO_ANULAR_ORDEN_PAGO);
        doNothing().when(compra).deleteComprobante(123L);

        compra.anularComprobante(123L, 5L);

        verify(compra).deleteComprobante(123L);
    }

    @Test
    void unaFacturaNoExigeElPrivilegioDeOrdenDePago() {
        comprobante(movimiento((byte) 0, null));
        when(entregaDetalleService.findAllByProveedorMovimientoId(123L)).thenReturn(List.of());
        doNothing().when(compra).deleteComprobante(123L);

        compra.anularComprobante(123L, 5L);

        verify(compra).deleteComprobante(123L);
        verifyNoInteractions(getPermisosEfectivosUseCase);
    }
}
