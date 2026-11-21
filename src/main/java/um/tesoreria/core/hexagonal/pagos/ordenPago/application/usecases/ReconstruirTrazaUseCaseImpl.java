package um.tesoreria.core.hexagonal.pagos.ordenPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ReservarOrdenContableUseCase;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.service.EjercicioService;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.pagos.entrega.application.service.EntregaService;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.Entrega;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.exception.OrdenPagoValidacionException;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.service.TrazaContableCalculator;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.*;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in.ReconstruirTrazaUseCase;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.service.ValorMovimientoService;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**
 * Reconstruye la traza contable de una orden de pago aplicando el plan del
 * {@link TrazaContableCalculator}: crea los asientos de los hijos que no los tienen y ajusta
 * las líneas faltantes o sobrantes de los asientos propios ya existentes.
 *
 * <p>Reglas de seguridad (la reconstrucción del legacy era destructiva):</p>
 * <ul>
 *   <li>Nunca borra líneas de otros comprobantes: si el asiento está compartido, exige
 *       {@code forzar} explícito.</li>
 *   <li>No renumera los items existentes: las líneas nuevas se agregan después del último.</li>
 *   <li>Es idempotente: vuelve a calcular el plan al terminar, de modo que una segunda
 *       ejecución devuelve "sin cambios".</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class ReconstruirTrazaUseCaseImpl implements ReconstruirTrazaUseCase {

    private static final String MENSAJE_FORZAR =
            "ERROR: La traza comparte asientos con otros comprobantes; confirme forzar";

    private final TrazaContableCalculator trazaContableCalculator;
    private final ProveedorMovimientoService proveedorMovimientoService;
    private final EntregaService entregaService;
    private final ValorMovimientoService valorMovimientoService;
    private final ReservarOrdenContableUseCase reservarOrdenContable;
    private final AsientoService asientoService;
    private final CuentaMovimientoService cuentaMovimientoService;
    private final EjercicioService ejercicioService;

    @Override
    @Transactional
    public ReconstruccionPlan reconstruir(Long proveedorMovimientoId, boolean forzar) {
        ReconstruccionPlan plan = trazaContableCalculator.calcular(proveedorMovimientoId);
        if (!plan.errores().isEmpty()) {
            throw new OrdenPagoValidacionException(plan.errores());
        }
        if (plan.requiereForzar() && !forzar) {
            throw new OrdenPagoValidacionException(List.of(MENSAJE_FORZAR));
        }
        if (plan.sinCambios()) {
            return plan;
        }
        ProveedorMovimiento ordenPago = proveedorMovimientoService
                .findByProveedorMovimientoId(proveedorMovimientoId);
        String vinculo = String.format("Pago %04d-%08d",
                ordenPago.getPrefijo() == null ? 0 : ordenPago.getPrefijo(),
                ordenPago.getNumeroComprobante() == null ? 0L : ordenPago.getNumeroComprobante());
        for (ReconstruccionAsiento asiento : plan.asientos()) {
            switch (asiento.accion()) {
                case CREAR -> crear(ordenPago, asiento, vinculo);
                case ACTUALIZAR -> actualizar(ordenPago, asiento);
                case SIN_CAMBIOS -> {
                }
            }
        }
        return trazaContableCalculator.calcular(proveedorMovimientoId);
    }

    private void crear(ProveedorMovimiento ordenPago, ReconstruccionAsiento asiento, String vinculo) {
        OffsetDateTime fecha = normalizar(asiento.fechaContable());
        validarEjercicio(fecha);
        Integer orden = reservarOrdenContable.reservar(fecha);
        Long proveedorMovimientoId = null;
        for (OrigenAsiento origen : asiento.origenes()) {
            switch (origen.tipo()) {
                case FACTURA -> proveedorMovimientoId = actualizarFactura(origen.referenciaId(), fecha, orden);
                case ENTREGA -> actualizarEntrega(origen.referenciaId(), fecha, orden);
                case VALOR -> {
                    valorMovimientoService.updateAsiento(origen.referenciaId(), fecha, orden);
                    proveedorMovimientoId = ordenPago.getProveedorMovimientoId();
                }
                case ENCABEZADO -> {
                    proveedorMovimientoId = ordenPago.getProveedorMovimientoId();
                    actualizarOrdenPago(ordenPago, fecha, orden);
                }
            }
        }
        asientoService.add(new AsientoEntity(null, fecha, orden, vinculo, null, null, null, null));
        int item = 0;
        for (LineaPropuesta linea : asiento.aAgregar()) {
            crearLinea(fecha, orden, ++item, linea, proveedorMovimientoId);
        }
    }

    private void actualizarOrdenPago(ProveedorMovimiento ordenPago, OffsetDateTime fecha, Integer orden) {
        if (orden.equals(ordenPago.getOrdenContable()) && fecha.equals(ordenPago.getFechaContable())) {
            return;
        }
        ordenPago.setFechaContable(fecha);
        ordenPago.setOrdenContable(orden);
        proveedorMovimientoService.updateProveedorMovimiento(ordenPago,
                ordenPago.getProveedorMovimientoId());
    }

    private void actualizar(ProveedorMovimiento ordenPago, ReconstruccionAsiento asiento) {
        asiento.idsEliminar().forEach(cuentaMovimientoService::deleteByCuentaMovimientoId);
        if (asiento.aAgregar().isEmpty()) {
            return;
        }
        OffsetDateTime fecha = normalizar(asiento.fechaContable());
        int item = maxItem(fecha, asiento.ordenContable());
        Long proveedorMovimientoId = proveedorMovimientoIdDe(ordenPago, asiento);
        for (LineaPropuesta linea : asiento.aAgregar()) {
            crearLinea(fecha, asiento.ordenContable(), ++item, linea, proveedorMovimientoId);
        }
    }

    /**
     * Igual que el legacy: las líneas de una factura llevan el id de la factura, las de los
     * valores y del encabezado el de la orden de pago, y las de asignación de costos ninguno.
     */
    private Long proveedorMovimientoIdDe(ProveedorMovimiento ordenPago, ReconstruccionAsiento asiento) {
        boolean conOrdenPago = false;
        for (OrigenAsiento origen : asiento.origenes()) {
            if (origen.tipo() == OrigenAsiento.Tipo.FACTURA) {
                return origen.referenciaId();
            }
            if (origen.tipo() == OrigenAsiento.Tipo.VALOR || origen.tipo() == OrigenAsiento.Tipo.ENCABEZADO) {
                conOrdenPago = true;
            }
        }
        return conOrdenPago ? ordenPago.getProveedorMovimientoId() : null;
    }

    private Long actualizarFactura(Long proveedorMovimientoId, OffsetDateTime fecha, Integer orden) {
        ProveedorMovimiento factura = proveedorMovimientoService
                .findByProveedorMovimientoId(proveedorMovimientoId);
        factura.setFechaContable(fecha);
        factura.setOrdenContable(orden);
        proveedorMovimientoService.updateProveedorMovimiento(factura, proveedorMovimientoId);
        return proveedorMovimientoId;
    }

    private void actualizarEntrega(Long entregaId, OffsetDateTime fecha, Integer orden) {
        Entrega entrega = entregaService.findById(entregaId);
        entregaService.updateAsiento(new Entrega(entrega.entregaId(), entrega.fecha(), entrega.ubicacionId(),
                entrega.recibe(), entrega.observacion(), fecha, orden, entrega.anulada(), entrega.tipo(),
                entrega.trackId()));
    }

    private void crearLinea(OffsetDateTime fecha, Integer orden, int item, LineaPropuesta linea,
                            Long proveedorMovimientoId) {
        cuentaMovimientoService.createCuentaMovimiento(CuentaMovimiento.builder()
                .fechaContable(fecha)
                .ordenContable(orden)
                .item(item)
                .numeroCuenta(linea.numeroCuenta())
                .debita(linea.debita())
                .comprobanteId(linea.comprobanteId())
                .concepto(linea.concepto())
                .importe(linea.importe())
                .proveedorId(linea.proveedorId())
                .proveedorMovimientoId(proveedorMovimientoId)
                .numeroAnulado(0)
                .version(0)
                .apertura((byte) 0)
                .build());
    }

    private int maxItem(OffsetDateTime fecha, Integer orden) {
        return cuentaMovimientoService.findAllByAsiento(fecha, orden, 0, 2).stream()
                .map(CuentaMovimiento::getItem)
                .filter(java.util.Objects::nonNull)
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }

    private void validarEjercicio(OffsetDateTime fecha) {
        LocalDate dia = fecha.toLocalDate();
        Optional<Ejercicio> ejercicio = ejercicioService.findByFecha(dia);
        if (ejercicio.isEmpty()) {
            throw new OrdenPagoValidacionException(
                    List.of("ERROR: No hay Ejercicio para la Fecha " + dia));
        }
        if (ejercicio.get().getBloqueado() != null && ejercicio.get().getBloqueado() == 1) {
            throw new OrdenPagoValidacionException(List.of("ERROR: Ejercicio BLOQUEADO"));
        }
    }

    private OffsetDateTime normalizar(OffsetDateTime fecha) {
        return fecha == null ? null
                : fecha.withOffsetSameInstant(ZoneOffset.UTC).toLocalDate()
                        .atStartOfDay().atOffset(ZoneOffset.UTC);
    }
}
