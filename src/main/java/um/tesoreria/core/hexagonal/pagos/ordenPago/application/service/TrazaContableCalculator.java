package um.tesoreria.core.hexagonal.pagos.ordenPago.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.AsientoPropuesto;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.AsientoPropuestoBuilder;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ContextoGasto;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.LineaPropuesta;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoContexto;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrigenAsiento;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ReconstruccionAccion;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ReconstruccionAsiento;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ReconstruccionPlan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Compara la traza contable propuesta por el motor con las líneas que hoy existen en cada
 * asiento y arma el plan de reconstrucción. No escribe nada: lo usan el preview (dry-run)
 * y la reconstrucción.
 *
 * <p>Reglas:</p>
 * <ul>
 *   <li>Sólo se consideran propias las líneas cuyo {@code comprobanteId} pertenece a alguno de
 *       los orígenes del asiento; las demás (de otros comprobantes del mismo asiento) nunca se
 *       marcan para borrar.</li>
 *   <li>Si un asiento se comparte con otros comprobantes y hay cambios, el plan exige
 *       confirmación explícita ({@code requiereForzar}).</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class TrazaContableCalculator {

    private static final Integer COMPROBANTE_COSTOS = 1;

    private final ProveedorMovimientoService proveedorMovimientoService;
    private final OrdenPagoPreparador ordenPagoPreparador;
    private final CuentaMovimientoService cuentaMovimientoService;

    public ReconstruccionPlan calcular(Long proveedorMovimientoId) {
        ProveedorMovimiento ordenPago = proveedorMovimientoService.findByProveedorMovimientoId(proveedorMovimientoId);
        List<AsientoPropuesto> propuestos = propuestasDe(ordenPago);
        List<ReconstruccionAsiento> asientos = new ArrayList<>();
        boolean requiereForzar = false;
        int crear = 0;
        int actualizar = 0;
        int sinCambios = 0;
        for (AsientoPropuesto propuesto : propuestos) {
            ReconstruccionAsiento asiento = comparar(propuesto, ordenPago);
            asientos.add(asiento);
            switch (asiento.accion()) {
                case CREAR -> crear++;
                case ACTUALIZAR -> actualizar++;
                default -> sinCambios++;
            }
            if (asiento.accion() == ReconstruccionAccion.ACTUALIZAR && asiento.compartido()) {
                requiereForzar = true;
            }
        }
        return new ReconstruccionPlan(proveedorMovimientoId, ordenPago.getPrefijo(),
                ordenPago.getNumeroComprobante(), asientos, crear, actualizar, sinCambios,
                requiereForzar, List.of());
    }

    /**
     * Una OP directa no tiene proveedor ni facturas: el legacy la reconstruye con
     * {@code makeAsientosDirecta}, tomando como gastos las líneas al debe de su propio asiento.
     */
    private List<AsientoPropuesto> propuestasDe(ProveedorMovimiento ordenPago) {
        if (ordenPago.getProveedorId() != null) {
            return AsientoPropuestoBuilder.buildOrdenPago(ordenPagoPreparador.contextoDe(ordenPago), false);
        }
        return AsientoPropuestoBuilder.buildDirecta(
                ordenPagoPreparador.contextoDirectaDe(ordenPago, gastosDe(ordenPago)), false);
    }

    private List<ContextoGasto> gastosDe(ProveedorMovimiento ordenPago) {
        if (ordenPago.getFechaContable() == null || ordenPago.getOrdenContable() == null
                || ordenPago.getOrdenContable() == 0) {
            return List.of();
        }
        return cuentaMovimientoService.findAllByAsiento(ordenPago.getFechaContable(), ordenPago.getOrdenContable(), 0, 2)
                .stream()
                .filter(linea -> COMPROBANTE_COSTOS.equals(linea.getComprobanteId()))
                .filter(linea -> Byte.valueOf((byte) 1).equals(linea.getDebita()))
                .map(linea -> new ContextoGasto(linea.getNumeroCuenta(), linea.getImporte()))
                .toList();
    }

    private ReconstruccionAsiento comparar(AsientoPropuesto propuesto, ProveedorMovimiento ordenPago) {
        if (propuesto.ordenContable() == null) {
            return new ReconstruccionAsiento(propuesto.fechaContable(), null, propuesto.origenes(),
                    ReconstruccionAccion.CREAR, propuesto.lineas(), List.of(),
                    propuesto.lineas().size(), 0, false);
        }
        List<CuentaMovimiento> existentes = cuentaMovimientoService
                .findAllByAsiento(propuesto.fechaContable(), propuesto.ordenContable(), 0, 2);
        List<Integer> permitidos = comprobantesPermitidos(propuesto.origenes(), ordenPago);
        List<CuentaMovimiento> propias = existentes.stream()
                .filter(linea -> permitidos.contains(linea.getComprobanteId()))
                .toList();
        boolean compartido = propias.size() < existentes.size();
        List<CuentaMovimiento> pendientes = new ArrayList<>(propias);
        List<LineaPropuesta> aAgregar = new ArrayList<>();
        for (LineaPropuesta linea : propuesto.lineas()) {
            CuentaMovimiento encontrada = pendientes.stream()
                    .filter(actual -> coincide(actual, linea))
                    .findFirst()
                    .orElse(null);
            if (encontrada == null) {
                aAgregar.add(linea);
            } else {
                pendientes.remove(encontrada);
            }
        }
        List<Long> idsEliminar = pendientes.stream()
                .map(CuentaMovimiento::getCuentaMovimientoId)
                .toList();
        ReconstruccionAccion accion = aAgregar.isEmpty() && idsEliminar.isEmpty()
                ? ReconstruccionAccion.SIN_CAMBIOS
                : ReconstruccionAccion.ACTUALIZAR;
        return new ReconstruccionAsiento(propuesto.fechaContable(), propuesto.ordenContable(),
                propuesto.origenes(), accion, aAgregar, idsEliminar,
                propuesto.lineas().size(), propias.size(), compartido);
    }

    /**
     * Comprobantes que pueden aparecer en las líneas propias de este asiento: el de cada factura,
     * el 1 de costos y, para los valores, tanto el comprobante de la orden de pago como el 1 que
     * usa la reconstrucción del legacy.
     */
    private List<Integer> comprobantesPermitidos(List<OrigenAsiento> origenes, ProveedorMovimiento ordenPago) {
        List<Integer> permitidos = new ArrayList<>();
        for (OrigenAsiento origen : origenes) {
            if (origen.tipo() == OrigenAsiento.Tipo.FACTURA) {
                Integer comprobanteId = proveedorMovimientoService
                        .findByProveedorMovimientoId(origen.referenciaId())
                        .getComprobanteId();
                agregarSiFalta(permitidos, comprobanteId);
            } else {
                agregarSiFalta(permitidos, COMPROBANTE_COSTOS);
                if (origen.tipo() == OrigenAsiento.Tipo.VALOR) {
                    agregarSiFalta(permitidos, ordenPago.getComprobanteId());
                }
            }
        }
        return permitidos;
    }

    private void agregarSiFalta(List<Integer> permitidos, Integer comprobanteId) {
        if (comprobanteId != null && !permitidos.contains(comprobanteId)) {
            permitidos.add(comprobanteId);
        }
    }

    private boolean coincide(CuentaMovimiento actual, LineaPropuesta propuesta) {
        return igual(actual.getNumeroCuenta(), propuesta.numeroCuenta())
                && Objects.equals(actual.getDebita(), propuesta.debita())
                && igual(actual.getImporte(), propuesta.importe())
                && Objects.equals(actual.getComprobanteId(), propuesta.comprobanteId());
    }

    private boolean igual(BigDecimal uno, BigDecimal otro) {
        if (uno == null || otro == null) {
            return uno == null && otro == null;
        }
        return uno.compareTo(otro) == 0;
    }
}
