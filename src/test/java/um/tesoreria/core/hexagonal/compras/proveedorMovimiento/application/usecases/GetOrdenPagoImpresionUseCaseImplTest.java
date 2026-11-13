package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ComprobanteNoImprimibleException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ProveedorMovimientoException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.OrdenPagoImpresion;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.OrdenPagoImpresion.TrazaLinea;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.TrazaContableOrdenPagoRepository;
import um.tesoreria.core.kotlin.model.ProveedorComprobante;
import um.tesoreria.core.kotlin.model.ProveedorValor;
import um.tesoreria.core.kotlin.model.ValorMovimiento;
import um.tesoreria.core.service.ProveedorComprobanteService;
import um.tesoreria.core.service.ProveedorValorService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetOrdenPagoImpresionUseCaseImplTest {

    @Mock
    private ProveedorMovimientoRepository repository;
    @Mock
    private ProveedorComprobanteService proveedorComprobanteService;
    @Mock
    private ProveedorValorService proveedorValorService;
    @Mock
    private TrazaContableOrdenPagoRepository trazaRepository;

    private GetOrdenPagoImpresionUseCaseImpl useCase() {
        return new GetOrdenPagoImpresionUseCaseImpl(repository, proveedorComprobanteService,
                proveedorValorService, trazaRepository);
    }

    private static ProveedorMovimiento movimiento(Long id, Byte ordenPago, String descripcion, int prefijo, long numero,
                                                  String fecha, String importe) {
        return ProveedorMovimiento.builder()
                .proveedorMovimientoId(id)
                .proveedorId(7)
                .prefijo(prefijo)
                .numeroComprobante(numero)
                .fechaComprobante(OffsetDateTime.parse(fecha))
                .importe(new BigDecimal(importe))
                .concepto("Compra de insumos")
                .comprobante(Comprobante.builder().comprobanteId(1).descripcion(descripcion).ordenPago(ordenPago).build())
                .proveedor(Proveedor.builder().proveedorId(7).razonSocial("ACME SA").domicilio("Calle 1").cuit("30-1-2").build())
                .build();
    }

    private static ProveedorComprobante aplicado(Long ordenId, Long facturaId, String importe) {
        ProveedorComprobante comprobante = new ProveedorComprobante();
        comprobante.setProveedorMovimientoIdOrdenPago(ordenId);
        comprobante.setProveedorMovimientoIdComprobante(facturaId);
        comprobante.setImporte(new BigDecimal(importe));
        return comprobante;
    }

    private static ProveedorValor valor(Long id, String importe) {
        ValorMovimiento valorMovimiento = new ValorMovimiento();
        valorMovimiento.setValorMovimientoId(id);
        valorMovimiento.setImporte(new BigDecimal(importe));
        ProveedorValor proveedorValor = new ProveedorValor();
        proveedorValor.setValorMovimiento(valorMovimiento);
        return proveedorValor;
    }

    @Test
    void unaOrdenDePagoDevuelveComprobantesValoresYTraza() {
        when(repository.findById(10L)).thenReturn(Optional.of(
                movimiento(10L, (byte) 1, "Orden de Pago", 15, 3684, "2026-11-02T00:00:00-03:00", "1000")));
        when(proveedorComprobanteService.findAllByOrdenPagoId(10L)).thenReturn(List.of(
                aplicado(10L, 21L, "300"), aplicado(10L, 20L, "700")));
        when(repository.findAllByIds(List.of(21L, 20L))).thenReturn(List.of(
                movimiento(20L, (byte) 0, "Factura A", 1, 5, "2026-10-01T00:00:00-03:00", "700"),
                movimiento(21L, (byte) 0, "Factura A", 1, 9, "2026-10-15T00:00:00-03:00", "500")));
        when(proveedorValorService.findAllByProveedorMovimientoId(10L)).thenReturn(List.of(
                valor(1L, "900"), valor(2L, "100")));
        when(trazaRepository.findConsolidadaByOrdenPagoId(10L)).thenReturn(List.of(
                new TrazaLinea(new BigDecimal("2110101"), "Proveedores", new BigDecimal("1000"), BigDecimal.ZERO),
                new TrazaLinea(new BigDecimal("1110101"), "Banco", BigDecimal.ZERO, new BigDecimal("1000"))));

        OrdenPagoImpresion impresion = useCase().getOrdenPagoImpresion(10L);

        assertThat(impresion.getNumero()).isEqualTo("0015-00003684");
        assertThat(impresion.getPrefijo()).isEqualTo(15);
        assertThat(impresion.getNumeroComprobante()).isEqualTo(3684L);
        assertThat(impresion.getRazonSocial()).isEqualTo("ACME SA");
        assertThat(impresion.getComprobantes()).extracting(OrdenPagoImpresion.ComprobanteImpreso::numeroComprobante)
                .containsExactly(5L, 9L);
        assertThat(impresion.getComprobantes().get(1).importe()).isEqualByComparingTo("500");
        assertThat(impresion.getComprobantes().get(1).importeAplicado()).isEqualByComparingTo("300");
        assertThat(impresion.getTotalComprobantes()).isEqualByComparingTo("1000");
        assertThat(impresion.getValores()).hasSize(2);
        assertThat(impresion.getTotalValores()).isEqualByComparingTo("1000");
        assertThat(impresion.getTraza()).hasSize(2);
        assertThat(impresion.getTotalDebe()).isEqualByComparingTo("1000");
        assertThat(impresion.getTotalHaber()).isEqualByComparingTo("1000");
    }

    @Test
    void unaOrdenSinComprobantesNiTrazaDevuelveListasVacias() {
        when(repository.findById(10L)).thenReturn(Optional.of(
                movimiento(10L, (byte) 1, "Orden de Pago", 15, 1, "2026-11-02T00:00:00-03:00", "0")));
        when(proveedorComprobanteService.findAllByOrdenPagoId(10L)).thenReturn(List.of());
        when(proveedorValorService.findAllByProveedorMovimientoId(10L)).thenReturn(List.of());
        when(trazaRepository.findConsolidadaByOrdenPagoId(10L)).thenReturn(List.of());

        OrdenPagoImpresion impresion = useCase().getOrdenPagoImpresion(10L);

        assertThat(impresion.getComprobantes()).isEmpty();
        assertThat(impresion.getValores()).isEmpty();
        assertThat(impresion.getTraza()).isEmpty();
        assertThat(impresion.getTotalComprobantes()).isEqualByComparingTo("0");
        assertThat(impresion.getTotalDebe()).isEqualByComparingTo("0");
    }

    @Test
    void unComprobanteQueNoEsOrdenDePagoSeRechaza() {
        when(repository.findById(5L)).thenReturn(Optional.of(
                movimiento(5L, (byte) 0, "Factura A", 1, 5, "2026-10-01T00:00:00-03:00", "700")));

        assertThatThrownBy(() -> useCase().getOrdenPagoImpresion(5L))
                .isInstanceOf(ComprobanteNoImprimibleException.class);
    }

    @Test
    void unComprobanteInexistenteSeRechaza() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase().getOrdenPagoImpresion(99L))
                .isInstanceOf(ProveedorMovimientoException.class);
    }
}