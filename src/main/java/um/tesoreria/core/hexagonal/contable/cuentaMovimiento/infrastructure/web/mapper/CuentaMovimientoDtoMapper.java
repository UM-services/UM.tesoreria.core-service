package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.web.dto.CuentaMovimientoRequest;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.web.dto.CuentaMovimientoResponse;

@Component
public class CuentaMovimientoDtoMapper {

    public CuentaMovimiento toDomain(CuentaMovimientoRequest request) {
        if (request == null) return null;
        return CuentaMovimiento.builder()
                .cuentaMovimientoId(request.getCuentaMovimientoId())
                .fechaContable(request.getFechaContable())
                .ordenContable(request.getOrdenContable())
                .item(request.getItem())
                .numeroCuenta(request.getNumeroCuenta())
                .debita(request.getDebita())
                .comprobanteId(request.getComprobanteId())
                .concepto(request.getConcepto())
                .importe(request.getImporte())
                .proveedorId(request.getProveedorId())
                .numeroAnulado(request.getNumeroAnulado())
                .version(request.getVersion())
                .proveedorMovimientoId(request.getProveedorMovimientoId())
                .proveedorMovimientoIdOrdenPago(request.getProveedorMovimientoIdOrdenPago())
                .apertura(request.getApertura())
                .trackId(request.getTrackId())
                .build();
    }

    public CuentaMovimientoResponse toResponse(CuentaMovimiento domain) {
        if (domain == null) return null;
        return CuentaMovimientoResponse.builder()
                .cuentaMovimientoId(domain.getCuentaMovimientoId())
                .fechaContable(domain.getFechaContable())
                .ordenContable(domain.getOrdenContable())
                .item(domain.getItem())
                .numeroCuenta(domain.getNumeroCuenta())
                .debita(domain.getDebita())
                .comprobanteId(domain.getComprobanteId())
                .concepto(domain.getConcepto())
                .importe(domain.getImporte())
                .proveedorId(domain.getProveedorId())
                .numeroAnulado(domain.getNumeroAnulado())
                .version(domain.getVersion())
                .proveedorMovimientoId(domain.getProveedorMovimientoId())
                .proveedorMovimientoIdOrdenPago(domain.getProveedorMovimientoIdOrdenPago())
                .apertura(domain.getApertura())
                .trackId(domain.getTrackId())
                .cuenta(domain.getCuenta())
                .proveedor(domain.getProveedor())
                .comprobante(domain.getComprobante())
                .proveedorMovimiento(domain.getProveedorMovimiento())
                .ordenPago(domain.getOrdenPago())
                .track(domain.getTrack())
                .build();
    }
}
