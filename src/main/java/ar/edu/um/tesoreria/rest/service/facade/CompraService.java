package ar.edu.um.tesoreria.rest.service.facade;

import ar.edu.um.tesoreria.rest.exception.facade.ContableException;
import ar.edu.um.tesoreria.rest.kotlin.model.*;
import ar.edu.um.tesoreria.rest.service.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CompraService {

    @Autowired
    private ProveedorPagoService proveedorPagoService;

    @Autowired
    private ProveedorMovimientoService proveedorMovimientoService;

    @Autowired
    private ProveedorValorService proveedorValorService;

    @Autowired
    private ProveedorArticuloService proveedorArticuloService;

    @Autowired
    private ValorMovimientoService valorMovimientoService;

    @Autowired
    private BancoMovimientoService bancoMovimientoService;

    @Autowired
    private ContableService contableService;

    @Transactional
    public void deleteComprobante(Long proveedorMovimientoId) {
        // Eliminar Aplicacion
        for (ProveedorPago proveedorPago : proveedorPagoService.findAllByPago(proveedorMovimientoId)) {
            ProveedorMovimiento proveedorMovimiento = proveedorMovimientoService.findByProveedorMovimientoId(proveedorPago.getProveedorMovimientoIdFactura());
            BigDecimal cancelado = proveedorMovimiento.getCancelado();
            cancelado = cancelado.subtract(proveedorPago.getImporteAplicado()).setScale(2, RoundingMode.HALF_UP);
            proveedorMovimiento.setCancelado(cancelado);
            proveedorMovimientoService.update(proveedorMovimiento, proveedorMovimiento.getProveedorMovimientoId());
            proveedorPagoService.delete(proveedorPago.getProveedorPagoId());
        }
        // Eliminar Valores
        for (ProveedorValor proveedorValor : proveedorValorService.findAllByProveedorMovimientoId(proveedorMovimientoId)) {
            ValorMovimiento valorMovimiento = valorMovimientoService.findByValorMovimientoId(proveedorValor.getValorMovimientoId());
            if (valorMovimiento.getFechaContable() != null) {
                if (!contableService.deleteAsiento(valorMovimiento.getFechaContable(), valorMovimiento.getOrdenContable())) {
                    throw new ContableException(valorMovimiento.getFechaContable(), valorMovimiento.getOrdenContable());
                }
            }
            BancoMovimiento bancoMovimiento = bancoMovimientoService.findByValorMovimientoId(valorMovimiento.getValorMovimientoId());
            if (bancoMovimiento != null) {
                bancoMovimientoService.deleteByBancoMovimientoId(bancoMovimiento.getBancoMovimientoId());
            }
            proveedorValorService.deleteByProveedorValorId(proveedorValor.getProveedorValorId());
            valorMovimientoService.deleteByValorMovimientoId(valorMovimiento.getValorMovimientoId());
        }
        // Eliminar Artículos
        for (ProveedorArticulo proveedorArticulo : proveedorArticuloService.findAllByProveedorMovimientoId(proveedorMovimientoId, false)){
            proveedorArticuloService.deleteByProveedorArticuloId(proveedorArticulo.getProveedorArticuloId());
        }
        // Eliminar Asiento
        ProveedorMovimiento proveedorMovimiento = proveedorMovimientoService.findByProveedorMovimientoId(proveedorMovimientoId);
        if (proveedorMovimiento.getFechaContable() != null) {
            if (!contableService.deleteAsiento(proveedorMovimiento.getFechaContable(), proveedorMovimiento.getOrdenContable())) {
                throw new ContableException(proveedorMovimiento.getFechaContable(), proveedorMovimiento.getOrdenContable());
            }
        }
        // Eliminar Comprobante
        proveedorMovimientoService.deleteByProveedorMovimientoId(proveedorMovimiento.getProveedorMovimientoId());
    }

}
