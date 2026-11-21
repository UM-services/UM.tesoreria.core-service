package um.tesoreria.core.hexagonal.pagos.ordenPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.pagos.entrega.application.service.EntregaService;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.EntregaAsignacion;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.*;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in.GetOrdenPagoDetalleUseCase;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.application.service.ProveedorPagoService;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.model.ProveedorPago;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.application.service.ProveedorValorService;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.model.ProveedorValor;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.service.ValorMovimientoService;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.service.ValorService;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.ValorMovimiento;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetOrdenPagoDetalleUseCaseImpl implements GetOrdenPagoDetalleUseCase {

    private final ProveedorMovimientoService proveedorMovimientoService;
    private final ProveedorPagoService proveedorPagoService;
    private final ProveedorValorService proveedorValorService;
    private final ValorMovimientoService valorMovimientoService;
    private final ValorService valorService;
    private final EntregaService entregaService;
    private final CuentaMovimientoService cuentaMovimientoService;

    @Override
    public OrdenPagoDetalle getDetalle(Long proveedorMovimientoId) {
        ProveedorMovimiento ordenPago = proveedorMovimientoService.findByProveedorMovimientoId(proveedorMovimientoId);
        List<ProveedorPago> pagos = proveedorPagoService.findAllByProveedorMovimientoIdPago(proveedorMovimientoId);

        List<OrdenPagoDetalleFactura> facturas = new ArrayList<>();
        List<Long> facturaIds = new ArrayList<>();
        Set<String> asientos = new LinkedHashSet<>();
        for (ProveedorPago pago : pagos) {
            ProveedorMovimiento factura = proveedorMovimientoService
                    .findByProveedorMovimientoId(pago.proveedorMovimientoIdFactura());
            facturaIds.add(factura.getProveedorMovimientoId());
            facturas.add(new OrdenPagoDetalleFactura(
                    factura.getProveedorMovimientoId(),
                    factura.getComprobanteId(),
                    factura.getPrefijo(),
                    factura.getNumeroComprobante(),
                    factura.getImporte(),
                    factura.getCancelado(),
                    pago.importeAplicado(),
                    pago.trazaContable(),
                    factura.getFechaContable(),
                    factura.getOrdenContable()));
            if (factura.getFechaContable() != null && factura.getOrdenContable() != null
                    && factura.getOrdenContable() > 0) {
                asientos.add(clave(factura.getFechaContable(), factura.getOrdenContable()));
            }
        }

        List<EntregaAsignacion> asignaciones = facturaIds.isEmpty()
                ? List.of()
                : entregaService.findAsignacionesByProveedorMovimientoId(facturaIds);
        for (EntregaAsignacion asignacion : asignaciones) {
            var entrega = asignacion.entrega();
            if (entrega.fechaContable() != null && entrega.ordenContable() != null && entrega.ordenContable() > 0) {
                asientos.add(clave(entrega.fechaContable(), entrega.ordenContable()));
            }
        }

        List<ProveedorValor> proveedorValores = proveedorValorService.findAllByProveedorMovimientoId(proveedorMovimientoId);
        List<Long> valorMovimientoIds = proveedorValores.stream()
                .map(ProveedorValor::valorMovimientoId)
                .filter(Objects::nonNull)
                .toList();
        Map<Long, ValorMovimiento> valoresById = valorMovimientoService.findAllByIdIn(valorMovimientoIds).stream()
                .collect(Collectors.toMap(ValorMovimiento::valorMovimientoId, Function.identity(), (a, b) -> a));

        List<OrdenPagoDetalleValor> valores = new ArrayList<>();
        for (ProveedorValor proveedorValor : proveedorValores) {
            ValorMovimiento valorMovimiento = valoresById.get(proveedorValor.valorMovimientoId());
            if (valorMovimiento == null) {
                continue;
            }
            String concepto = valorService.findValorById(valorMovimiento.valorId())
                    .map(valor -> valor.concepto())
                    .orElse(null);
            valores.add(new OrdenPagoDetalleValor(
                    valorMovimiento.valorMovimientoId(),
                    valorMovimiento.valorId(),
                    concepto,
                    valorMovimiento.numero(),
                    valorMovimiento.fechaEmision(),
                    valorMovimiento.importe(),
                    valorMovimiento.estado(),
                    valorMovimiento.fechaContable(),
                    valorMovimiento.ordenContable()));
            if (valorMovimiento.fechaContable() != null && valorMovimiento.ordenContable() != null
                    && valorMovimiento.ordenContable() > 0) {
                asientos.add(clave(valorMovimiento.fechaContable(), valorMovimiento.ordenContable()));
            }
        }

        List<OrdenPagoDetalleLinea> traza = new ArrayList<>();
        for (String asiento : asientos) {
            String[] partes = asiento.split("\\|");
            OffsetDateTime fecha = OffsetDateTime.parse(partes[0]);
            Integer orden = Integer.valueOf(partes[1]);
            for (CuentaMovimiento cuentaMovimiento : cuentaMovimientoService.findAllByAsiento(fecha, orden, 0, 2)) {
                traza.add(new OrdenPagoDetalleLinea(
                        cuentaMovimiento.getFechaContable(),
                        cuentaMovimiento.getOrdenContable(),
                        cuentaMovimiento.getItem(),
                        cuentaMovimiento.getNumeroCuenta(),
                        cuentaMovimiento.getDebita(),
                        cuentaMovimiento.getImporte(),
                        cuentaMovimiento.getComprobanteId(),
                        cuentaMovimiento.getConcepto()));
            }
        }
        traza.sort(Comparator
                .comparing(OrdenPagoDetalleLinea::fechaContable)
                .thenComparing(OrdenPagoDetalleLinea::ordenContable)
                .thenComparing(OrdenPagoDetalleLinea::item));

        return new OrdenPagoDetalle(
                ordenPago.getProveedorMovimientoId(),
                ordenPago.getProveedorId(),
                ordenPago.getNombreBeneficiario(),
                ordenPago.getComprobanteId(),
                ordenPago.getPrefijo(),
                ordenPago.getNumeroComprobante(),
                ordenPago.getFechaComprobante(),
                ordenPago.getFechaVencimiento(),
                ordenPago.getImporte(),
                ordenPago.getCancelado(),
                ordenPago.getConcepto(),
                ordenPago.getConCargo(),
                ordenPago.getSolicitaFactura(),
                ordenPago.getGeograficaId(),
                ordenPago.getFechaContable(),
                ordenPago.getOrdenContable(),
                facturas,
                valores,
                traza);
    }

    private String clave(OffsetDateTime fecha, Integer orden) {
        return fecha + "|" + orden;
    }
}
