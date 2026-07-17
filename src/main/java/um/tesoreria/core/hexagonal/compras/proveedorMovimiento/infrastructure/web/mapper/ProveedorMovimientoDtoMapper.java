package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.comprobante.infrastructure.web.mapper.ComprobanteDtoMapper;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.web.mapper.ProveedorDtoMapper;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto.ProveedorMovimientoRequest;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto.ProveedorMovimientoResponse;
import um.tesoreria.core.hexagonal.geografica.infrastructure.web.mapper.GeograficaDtoMapper;

import java.util.stream.Collectors;

@Component
public class ProveedorMovimientoDtoMapper {

    private final ComprobanteDtoMapper comprobanteDtoMapper;
    private final ProveedorDtoMapper proveedorDtoMapper;
    private final GeograficaDtoMapper geograficaDtoMapper;

    public ProveedorMovimientoDtoMapper(ComprobanteDtoMapper comprobanteDtoMapper, ProveedorDtoMapper proveedorDtoMapper, GeograficaDtoMapper geograficaDtoMapper) {
        this.comprobanteDtoMapper = comprobanteDtoMapper;
        this.proveedorDtoMapper = proveedorDtoMapper;
        this.geograficaDtoMapper = geograficaDtoMapper;
    }

    public ProveedorMovimiento toDomain(ProveedorMovimientoRequest request) {
        if (request == null) return null;
        return ProveedorMovimiento.builder()
                .proveedorId(request.getProveedorId())
                .nombreBeneficiario(request.getNombreBeneficiario())
                .comprobanteId(request.getComprobanteId())
                .fechaComprobante(request.getFechaComprobante())
                .fechaVencimiento(request.getFechaVencimiento())
                .prefijo(request.getPrefijo())
                .numeroComprobante(request.getNumeroComprobante())
                .netoSinDescuento(request.getNetoSinDescuento())
                .descuento(request.getDescuento())
                .neto(request.getNeto())
                .importe(request.getImporte())
                .cancelado(request.getCancelado())
                .fechaContable(request.getFechaContable())
                .ordenContable(request.getOrdenContable())
                .concepto(request.getConcepto())
                .fechaAnulacion(request.getFechaAnulacion())
                .conCargo(request.getConCargo())
                .solicitaFactura(request.getSolicitaFactura())
                .geograficaId(request.getGeograficaId())
                .build();
    }

    public ProveedorMovimientoResponse toResponse(ProveedorMovimiento domain) {
        if (domain == null) return null;
        return ProveedorMovimientoResponse.builder()
                .proveedorMovimientoId(domain.getProveedorMovimientoId())
                .proveedorId(domain.getProveedorId())
                .nombreBeneficiario(domain.getNombreBeneficiario())
                .comprobanteId(domain.getComprobanteId())
                .fechaComprobante(domain.getFechaComprobante())
                .fechaVencimiento(domain.getFechaVencimiento())
                .prefijo(domain.getPrefijo())
                .numeroComprobante(domain.getNumeroComprobante())
                .netoSinDescuento(domain.getNetoSinDescuento())
                .descuento(domain.getDescuento())
                .neto(domain.getNeto())
                .importe(domain.getImporte())
                .cancelado(domain.getCancelado())
                .fechaContable(domain.getFechaContable())
                .ordenContable(domain.getOrdenContable())
                .concepto(domain.getConcepto())
                .fechaAnulacion(domain.getFechaAnulacion())
                .conCargo(domain.getConCargo())
                .solicitaFactura(domain.getSolicitaFactura())
                .geograficaId(domain.getGeograficaId())
                .comprobante(comprobanteDtoMapper.toResponse(domain.getComprobante()))
                .proveedor(proveedorDtoMapper.toResponse(domain.getProveedor()))
                .geografica(geograficaDtoMapper.toResponse(domain.getGeografica()))
                .proveedorArticulos(domain.getProveedorArticulos() != null ? domain.getProveedorArticulos().stream().map(a -> a).collect(Collectors.toList()) : null)
                .ordenPagos(domain.getOrdenPagos() != null ? domain.getOrdenPagos().stream().map(p -> p).collect(Collectors.toList()) : null)
                .build();
    }
}
