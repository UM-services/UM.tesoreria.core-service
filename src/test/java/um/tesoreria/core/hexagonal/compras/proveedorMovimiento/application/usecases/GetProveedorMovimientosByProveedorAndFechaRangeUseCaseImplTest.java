package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.AnulacionComprobante;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimientoResumen;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;
import um.tesoreria.core.kotlin.model.EntregaDetalle;
import um.tesoreria.core.service.EntregaDetalleService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProveedorMovimientosByProveedorAndFechaRangeUseCaseImplTest {

    private static final OffsetDateTime DESDE = OffsetDateTime.parse("2026-01-01T00:00:00-03:00");
    private static final OffsetDateTime HASTA = OffsetDateTime.parse("2026-12-31T00:00:00-03:00");

    @Mock
    private ProveedorMovimientoRepository repository;
    @Mock
    private EntregaDetalleService entregaDetalleService;

    private GetProveedorMovimientosByProveedorAndFechaRangeUseCaseImpl useCase() {
        return new GetProveedorMovimientosByProveedorAndFechaRangeUseCaseImpl(repository, entregaDetalleService);
    }

    private static ProveedorMovimiento movimiento(Long id, Integer prefijo, Long numero, String fecha,
                                                  String importe, String cancelado, Byte ordenPago,
                                                  OffsetDateTime fechaAnulacion) {
        return ProveedorMovimiento.builder()
                .proveedorMovimientoId(id)
                .proveedorId(7)
                .comprobanteId(1)
                .prefijo(prefijo)
                .numeroComprobante(numero)
                .fechaComprobante(OffsetDateTime.parse(fecha))
                .importe(new BigDecimal(importe))
                .cancelado(new BigDecimal(cancelado))
                .fechaAnulacion(fechaAnulacion)
                .comprobante(Comprobante.builder()
                        .comprobanteId(1)
                        .descripcion(ordenPago != null && ordenPago != 0 ? "Orden de Pago" : "Factura A")
                        .ordenPago(ordenPago)
                        .build())
                .build();
    }

    @Test
    void ordenaPorFechaDescendenteYLasOrdenesDePagoPrimero() {
        ProveedorMovimiento factura = movimiento(1L, 1, 10L, "2026-10-02T00:00:00-03:00", "100", "30", (byte) 0, null);
        ProveedorMovimiento ordenPago = movimiento(2L, 2, 20L, "2026-10-02T00:00:00-03:00", "50", "50", (byte) 1, null);
        ProveedorMovimiento vieja = movimiento(3L, 1, 5L, "2026-09-01T00:00:00-03:00", "80", "0", (byte) 0, null);
        when(repository.findAllByProveedorIdAndFechaComprobanteBetween(7, DESDE, HASTA))
                .thenReturn(List.of(factura, ordenPago, vieja));
        when(entregaDetalleService.findAllByProveedorMovimientoIds(anyList())).thenReturn(List.of());

        List<ProveedorMovimientoResumen> resumenes = useCase().getProveedorMovimientos(7, DESDE, HASTA, 0);

        assertThat(resumenes).extracting(ProveedorMovimientoResumen::getProveedorMovimientoId)
                .containsExactly(2L, 1L, 3L);
    }

    @Test
    void calculaSaldoNumeroYEstadoDeAnulacion() {
        ProveedorMovimiento factura = movimiento(1L, 1, 10L, "2026-10-02T00:00:00-03:00", "100.50", "30.25", (byte) 0, null);
        when(repository.findAllByProveedorIdAndFechaComprobanteBetween(7, DESDE, HASTA))
                .thenReturn(List.of(factura));
        when(entregaDetalleService.findAllByProveedorMovimientoIds(anyList())).thenReturn(List.of());

        ProveedorMovimientoResumen resumen = useCase().getProveedorMovimientos(7, DESDE, HASTA, 0).get(0);

        assertThat(resumen.getNumero()).isEqualTo("0001-00000010");
        assertThat(resumen.getSaldo()).isEqualByComparingTo("70.25");
        assertThat(resumen.getAnulable()).isTrue();
        assertThat(resumen.getMotivosNoAnulable()).isEmpty();
        assertThat(resumen.getRequierePrivilegioOp()).isFalse();
        assertThat(resumen.getComprobanteDescripcion()).isEqualTo("Factura A");
    }

    @Test
    void unaFacturaConCostosAsignadosNoEsAnulable() {
        ProveedorMovimiento factura = movimiento(1L, 1, 10L, "2026-10-02T00:00:00-03:00", "100", "0", (byte) 0, null);
        when(repository.findAllByProveedorIdAndFechaComprobanteBetween(7, DESDE, HASTA))
                .thenReturn(List.of(factura));
        EntregaDetalle detalle = new EntregaDetalle();
        detalle.setProveedorMovimientoId(1L);
        when(entregaDetalleService.findAllByProveedorMovimientoIds(anyList())).thenReturn(List.of(detalle));

        ProveedorMovimientoResumen resumen = useCase().getProveedorMovimientos(7, DESDE, HASTA, 0).get(0);

        assertThat(resumen.getAnulable()).isFalse();
        assertThat(resumen.getMotivosNoAnulable()).containsExactly(AnulacionComprobante.COSTOS_ASIGNADOS);
    }

    @Test
    void unComprobanteAnuladoNoEsAnulable() {
        ProveedorMovimiento anulada = movimiento(1L, 1, 10L, "2026-10-02T00:00:00-03:00", "100", "0", (byte) 0,
                OffsetDateTime.parse("2026-10-05T00:00:00-03:00"));
        when(repository.findAllByProveedorIdAndFechaComprobanteBetween(7, DESDE, HASTA))
                .thenReturn(List.of(anulada));
        when(entregaDetalleService.findAllByProveedorMovimientoIds(anyList())).thenReturn(List.of());

        ProveedorMovimientoResumen resumen = useCase().getProveedorMovimientos(7, DESDE, HASTA, 0).get(0);

        assertThat(resumen.getAnulable()).isFalse();
        assertThat(resumen.getMotivosNoAnulable()).containsExactly(AnulacionComprobante.YA_ANULADA);
    }

    @Test
    void unaOrdenDePagoExigeElPrivilegioAdicional() {
        ProveedorMovimiento ordenPago = movimiento(2L, 2, 20L, "2026-10-02T00:00:00-03:00", "50", "50", (byte) 1, null);
        when(repository.findAllByProveedorIdAndFechaComprobanteBetween(7, DESDE, HASTA))
                .thenReturn(List.of(ordenPago));
        when(entregaDetalleService.findAllByProveedorMovimientoIds(anyList())).thenReturn(List.of());

        ProveedorMovimientoResumen resumen = useCase().getProveedorMovimientos(7, DESDE, HASTA, 0).get(0);

        assertThat(resumen.getRequierePrivilegioOp()).isTrue();
        assertThat(resumen.getOrdenPago()).isEqualTo((byte) 1);
    }

    @Test
    void conSedeConsultaPorGeografica() {
        when(repository.findAllByProveedorIdAndFechaComprobanteBetweenAndGeograficaId(7, DESDE, HASTA, 3))
                .thenReturn(List.of());

        assertThat(useCase().getProveedorMovimientos(7, DESDE, HASTA, 3)).isEmpty();

        verify(repository, never()).findAllByProveedorIdAndFechaComprobanteBetween(7, DESDE, HASTA);
        verifyNoInteractions(entregaDetalleService);
    }
}
