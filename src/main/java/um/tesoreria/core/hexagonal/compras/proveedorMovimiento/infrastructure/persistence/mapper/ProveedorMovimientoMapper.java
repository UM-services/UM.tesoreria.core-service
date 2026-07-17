package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.comprobante.infrastructure.persistence.mapper.ComprobanteMapper;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.mapper.ProveedorMapper;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.entity.ProveedorMovimientoEntity;
import um.tesoreria.core.hexagonal.geografica.infrastructure.persistence.mapper.GeograficaMapper;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProveedorMovimientoMapper {

    private final ComprobanteMapper comprobanteMapper;
    private final ProveedorMapper proveedorMapper;
    private final GeograficaMapper geograficaMapper;

    public ProveedorMovimiento toDomainModel(ProveedorMovimientoEntity entity) {
        if (entity == null) return null;
        return ProveedorMovimiento.builder()
                .proveedorMovimientoId(entity.getProveedorMovimientoId())
                .proveedorId(entity.getProveedorId())
                .nombreBeneficiario(entity.getNombreBeneficiario())
                .comprobanteId(entity.getComprobanteId())
                .fechaComprobante(entity.getFechaComprobante())
                .fechaVencimiento(entity.getFechaVencimiento())
                .prefijo(entity.getPrefijo())
                .numeroComprobante(entity.getNumeroComprobante())
                .netoSinDescuento(entity.getNetoSinDescuento())
                .descuento(entity.getDescuento())
                .neto(entity.getNeto())
                .importe(entity.getImporte())
                .cancelado(entity.getCancelado())
                .fechaContable(entity.getFechaContable())
                .ordenContable(entity.getOrdenContable())
                .concepto(entity.getConcepto())
                .fechaAnulacion(entity.getFechaAnulacion())
                .conCargo(entity.getConCargo())
                .solicitaFactura(entity.getSolicitaFactura())
                .geograficaId(entity.getGeograficaId())
                .comprobante(comprobanteMapper.toDomainModel(entity.getComprobante()))
                .proveedor(proveedorMapper.toDomainModel(entity.getProveedor()))
                .geografica(geograficaMapper.toDomainModel(entity.getGeografica()))
                .proveedorArticulos(entity.getProveedorArticulos() != null ? entity.getProveedorArticulos().stream().map(a -> a).collect(Collectors.toList()) : null)
                .ordenPagos(entity.getOrdenPagos() != null ? entity.getOrdenPagos().stream().map(p -> p).collect(Collectors.toList()) : null)
                .build();
    }

    public ProveedorMovimientoEntity toEntity(ProveedorMovimiento domain) {
        if (domain == null) return null;
        ProveedorMovimientoEntity.ProveedorMovimientoEntityBuilder builder = ProveedorMovimientoEntity.builder()
                .proveedorMovimientoId(domain.getProveedorMovimientoId())
                .proveedorId(domain.getProveedorId())
                .comprobanteId(domain.getComprobanteId())
                .fechaComprobante(domain.getFechaComprobante())
                .fechaVencimiento(domain.getFechaVencimiento())
                .netoSinDescuento(domain.getNetoSinDescuento())
                .descuento(domain.getDescuento())
                .neto(domain.getNeto())
                .importe(domain.getImporte())
                .cancelado(domain.getCancelado())
                .fechaContable(domain.getFechaContable())
                .ordenContable(domain.getOrdenContable())
                .concepto(domain.getConcepto())
                .fechaAnulacion(domain.getFechaAnulacion())
                .geograficaId(domain.getGeograficaId());

        if (domain.getNombreBeneficiario() != null) builder.nombreBeneficiario(domain.getNombreBeneficiario());
        if (domain.getPrefijo() != null) builder.prefijo(domain.getPrefijo());
        if (domain.getNumeroComprobante() != null) builder.numeroComprobante(domain.getNumeroComprobante());
        if (domain.getConCargo() != null) builder.conCargo(domain.getConCargo());
        if (domain.getSolicitaFactura() != null) builder.solicitaFactura(domain.getSolicitaFactura());

        return builder.build();
    }
}
