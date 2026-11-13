package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ProveedorMovimientoException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.CompraImpresion;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.OrdenPagoImpresion;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimientoDetalle;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimientoResumen;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProveedorMovimientoService {

    private final CreateProveedorMovimientoUseCase createProveedorMovimientoUseCase;
    private final UpdateProveedorMovimientoUseCase updateProveedorMovimientoUseCase;
    private final DeleteProveedorMovimientoUseCase deleteProveedorMovimientoUseCase;
    private final GetProveedorMovimientoByIdUseCase getProveedorMovimientoByIdUseCase;
    private final GetProveedorMovimientoByOrdenPagoUseCase getProveedorMovimientoByOrdenPagoUseCase;
    private final GetLastProveedorMovimientoByOrdenPagoUseCase getLastProveedorMovimientoByOrdenPagoUseCase;
    private final GetProveedorMovimientosByIdsUseCase getProveedorMovimientosByIdsUseCase;
    private final GetProveedorMovimientosByProveedorUseCase getProveedorMovimientosByProveedorUseCase;
    private final GetProveedorMovimientosByComprobanteAndFechaRangeUseCase getProveedorMovimientosByComprobanteAndFechaRangeUseCase;
    private final GetProveedorMovimientosEliminablesUseCase getProveedorMovimientosEliminablesUseCase;
    private final GetProveedorMovimientosAsignablesUseCase getProveedorMovimientosAsignablesUseCase;
    private final GetProveedorMovimientosDisponiblesUseCase getProveedorMovimientosDisponiblesUseCase;
    private final GetProveedorMovimientoIdsForCostAdjustmentUseCase getProveedorMovimientoIdsForCostAdjustmentUseCase;
    private final GetProveedorMovimientosByProveedorAndFechaRangeUseCase getProveedorMovimientosByProveedorAndFechaRangeUseCase;
    private final GetProveedorMovimientoDetalleUseCase getProveedorMovimientoDetalleUseCase;
    private final GetOrdenPagoImpresionUseCase getOrdenPagoImpresionUseCase;
    private final GetCompraImpresionUseCase getCompraImpresionUseCase;

    public ProveedorMovimiento createProveedorMovimiento(ProveedorMovimiento proveedorMovimiento) {
        return createProveedorMovimientoUseCase.createProveedorMovimiento(proveedorMovimiento);
    }

    public ProveedorMovimiento updateProveedorMovimiento(ProveedorMovimiento proveedorMovimiento, Long proveedorMovimientoId) {
        return updateProveedorMovimientoUseCase.updateProveedorMovimiento(proveedorMovimiento, proveedorMovimientoId);
    }

    public void deleteProveedorMovimiento(Long proveedorMovimientoId) {
        deleteProveedorMovimientoUseCase.deleteProveedorMovimiento(proveedorMovimientoId);
    }

    public void deleteProveedorMovimiento(ProveedorMovimiento proveedorMovimiento) {
        deleteProveedorMovimientoUseCase.deleteProveedorMovimiento(proveedorMovimiento);
    }

    public ProveedorMovimiento findByProveedorMovimientoId(Long proveedorMovimientoId) {
        return getProveedorMovimientoByIdUseCase.getProveedorMovimientoById(proveedorMovimientoId)
                .orElseThrow(() -> new ProveedorMovimientoException(proveedorMovimientoId));
    }

    public ProveedorMovimiento findByOrdenPago(Integer prefijo, Long numeroComprobante) {
        return getProveedorMovimientoByOrdenPagoUseCase.getProveedorMovimientoByOrdenPago(prefijo, numeroComprobante)
                .orElseThrow(() -> new ProveedorMovimientoException(prefijo, numeroComprobante));
    }

    public ProveedorMovimiento findLastOrdenPago(Integer prefijo) {
        return getLastProveedorMovimientoByOrdenPagoUseCase.getLastProveedorMovimientoByOrdenPago(prefijo)
                .orElseThrow(() -> new ProveedorMovimientoException(prefijo));
    }

    public List<ProveedorMovimiento> findAllByProveedorMovimientoIdIn(List<Long> proveedorMovimientoIds) {
        return getProveedorMovimientosByIdsUseCase.getProveedorMovimientosByIds(proveedorMovimientoIds);
    }

    public List<ProveedorMovimiento> findAllByProveedorId(Integer proveedorId, Integer geograficaId) {
        return getProveedorMovimientosByProveedorUseCase.getProveedorMovimientosByProveedor(proveedorId, geograficaId);
    }

    public List<ProveedorMovimiento> findAllByComprobanteIdAndFechaComprobanteBetween(
            Integer comprobanteId, OffsetDateTime fechaInicio, OffsetDateTime fechaFinal) {
        return getProveedorMovimientosByComprobanteAndFechaRangeUseCase
                .getProveedorMovimientosByComprobanteAndFechaRange(comprobanteId, fechaInicio, fechaFinal);
    }

    public List<ProveedorMovimiento> findAllEliminables(Integer ejercicioId) {
        return getProveedorMovimientosEliminablesUseCase.getProveedorMovimientosEliminables(ejercicioId);
    }

    public List<ProveedorMovimiento> findAllAsignables(Integer proveedorId, OffsetDateTime desde,
                                                       OffsetDateTime hasta, Integer geograficaId, Boolean todos) {
        return getProveedorMovimientosAsignablesUseCase
                .getProveedorMovimientosAsignables(proveedorId, desde, hasta, geograficaId, todos);
    }

    public List<ProveedorMovimiento> findAllByNotOrdenPagoBetweenAndNotAnulado(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta) {
        return getProveedorMovimientosDisponiblesUseCase.getProveedorMovimientosDisponibles(fechaDesde, fechaHasta);
    }

    public List<Long> findDistinctProveedorMovimientoIdsForCostAdjustment() {
        return getProveedorMovimientoIdsForCostAdjustmentUseCase.getProveedorMovimientoIdsForCostAdjustment();
    }

    public List<ProveedorMovimientoResumen> findAllByProveedorAndFechaRange(Integer proveedorId, OffsetDateTime desde,
                                                                            OffsetDateTime hasta, Integer geograficaId) {
        return getProveedorMovimientosByProveedorAndFechaRangeUseCase
                .getProveedorMovimientos(proveedorId, desde, hasta, geograficaId);
    }

    public ProveedorMovimientoDetalle findDetalle(Long proveedorMovimientoId) {
        return getProveedorMovimientoDetalleUseCase.getProveedorMovimientoDetalle(proveedorMovimientoId);
    }

    public OrdenPagoImpresion findOrdenPagoImpresion(Long proveedorMovimientoId) {
        return getOrdenPagoImpresionUseCase.getOrdenPagoImpresion(proveedorMovimientoId);
    }

    public CompraImpresion findCompraImpresion(Long proveedorMovimientoId) {
        return getCompraImpresionUseCase.getCompraImpresion(proveedorMovimientoId);
    }
}