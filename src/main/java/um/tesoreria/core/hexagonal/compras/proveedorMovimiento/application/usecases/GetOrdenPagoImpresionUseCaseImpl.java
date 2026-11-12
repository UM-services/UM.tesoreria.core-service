package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ComprobanteNoImprimibleException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ProveedorMovimientoException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.OrdenPagoImpresion;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.OrdenPagoImpresion.ComprobanteImpreso;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.OrdenPagoImpresion.TrazaLinea;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in.GetOrdenPagoImpresionUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.TrazaContableOrdenPagoRepository;
import um.tesoreria.core.kotlin.model.ProveedorComprobante;
import um.tesoreria.core.kotlin.model.ProveedorValor;
import um.tesoreria.core.kotlin.model.ValorMovimiento;
import um.tesoreria.core.service.ProveedorComprobanteService;
import um.tesoreria.core.service.ProveedorValorService;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Datos de impresión de una orden de pago. Sólo lectura: el {@code /detalle}
 * que usa la pantalla no cambia.
 */
@Component
@RequiredArgsConstructor
public class GetOrdenPagoImpresionUseCaseImpl implements GetOrdenPagoImpresionUseCase {

    private final ProveedorMovimientoRepository repository;
    private final ProveedorComprobanteService proveedorComprobanteService;
    private final ProveedorValorService proveedorValorService;
    private final TrazaContableOrdenPagoRepository trazaRepository;

    @Override
    public OrdenPagoImpresion getOrdenPagoImpresion(Long proveedorMovimientoId) {
        ProveedorMovimiento orden = repository.findById(proveedorMovimientoId)
                .orElseThrow(() -> new ProveedorMovimientoException(proveedorMovimientoId));
        if (!ComprobanteFormato.esOrdenPago(orden)) {
            throw new ComprobanteNoImprimibleException(proveedorMovimientoId);
        }

        List<ComprobanteImpreso> comprobantes = comprobantes(proveedorMovimientoId);
        List<ValorMovimiento> valores = valores(proveedorMovimientoId);
        List<TrazaLinea> traza = trazaRepository.findConsolidadaByOrdenPagoId(proveedorMovimientoId);

        var proveedor = orden.getProveedor();

        return OrdenPagoImpresion.builder()
                .proveedorMovimientoId(proveedorMovimientoId)
                .comprobanteDescripcion(ComprobanteFormato.descripcion(orden))
                .numero(ComprobanteFormato.numero(orden))
                .prefijo(orden.getPrefijo())
                .numeroComprobante(orden.getNumeroComprobante())
                .fechaComprobante(orden.getFechaComprobante())
                .fechaAnulacion(orden.getFechaAnulacion())
                .concepto(orden.getConcepto())
                .nombreBeneficiario(orden.getNombreBeneficiario())
                .proveedorId(orden.getProveedorId())
                .razonSocial(proveedor != null ? proveedor.getRazonSocial() : null)
                .domicilio(proveedor != null ? proveedor.getDomicilio() : null)
                .cuit(proveedor != null ? proveedor.getCuit() : null)
                .comprobantes(comprobantes)
                .totalComprobantes(sumar(comprobantes.stream().map(ComprobanteImpreso::importeAplicado)))
                .valores(valores)
                .totalValores(sumar(valores.stream().map(ValorMovimiento::getImporte)))
                .traza(traza)
                .totalDebe(sumar(traza.stream().map(TrazaLinea::debe)))
                .totalHaber(sumar(traza.stream().map(TrazaLinea::haber)))
                .build();
    }

    private List<ComprobanteImpreso> comprobantes(Long ordenPagoId) {
        List<ProveedorComprobante> aplicados = proveedorComprobanteService.findAllByOrdenPagoId(ordenPagoId);
        if (aplicados.isEmpty()) {
            return List.of();
        }
        List<Long> ids = aplicados.stream()
                .map(ProveedorComprobante::getProveedorMovimientoIdComprobante)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, ProveedorMovimiento> comprobantes = repository.findAllByIds(ids).stream()
                .collect(Collectors.toMap(ProveedorMovimiento::getProveedorMovimientoId, Function.identity(),
                        (a, b) -> a));

        return aplicados.stream()
                .map(aplicado -> {
                    ProveedorMovimiento comprobante = comprobantes.get(aplicado.getProveedorMovimientoIdComprobante());
                    return comprobante == null
                            ? new ComprobanteImpreso(aplicado.getProveedorMovimientoIdComprobante(), "", null, null, null,
                            BigDecimal.ZERO, ComprobanteFormato.valor(aplicado.getImporte()))
                            : new ComprobanteImpreso(
                            comprobante.getProveedorMovimientoId(),
                            ComprobanteFormato.descripcion(comprobante),
                            comprobante.getPrefijo(),
                            comprobante.getNumeroComprobante(),
                            comprobante.getFechaComprobante(),
                            ComprobanteFormato.valor(comprobante.getImporte()),
                            ComprobanteFormato.valor(aplicado.getImporte()));
                })
                .sorted(Comparator
                        .comparing(ComprobanteImpreso::fechaComprobante, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(ComprobanteImpreso::prefijo, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(ComprobanteImpreso::numeroComprobante, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    private List<ValorMovimiento> valores(Long ordenPagoId) {
        return proveedorValorService.findAllByProveedorMovimientoId(ordenPagoId).stream()
                .map(ProveedorValor::getValorMovimiento)
                .filter(Objects::nonNull)
                .toList();
    }

    private static BigDecimal sumar(java.util.stream.Stream<BigDecimal> importes) {
        return importes.filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}