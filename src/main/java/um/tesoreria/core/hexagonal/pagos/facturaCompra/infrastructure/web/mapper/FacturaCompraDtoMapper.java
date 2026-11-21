package um.tesoreria.core.hexagonal.pagos.facturaCompra.infrastructure.web.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.mapper.AsientoContableDtoMapper;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model.FacturaCompra;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model.FacturaCompraArticulo;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model.FacturaCompraArticuloDetalle;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model.FacturaCompraDetalle;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model.FacturaCompraResultado;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.infrastructure.web.dto.FacturaCompraArticuloRequest;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.infrastructure.web.dto.FacturaCompraArticuloResponse;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.infrastructure.web.dto.FacturaCompraDetalleResponse;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.infrastructure.web.dto.FacturaCompraRequest;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.infrastructure.web.dto.FacturaCompraResponse;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FacturaCompraDtoMapper {

    private final AsientoContableDtoMapper asientoMapper;

    public FacturaCompra toDomain(FacturaCompraRequest request) {
        return new FacturaCompra(
                request.proveedorId(),
                request.comprobanteId(),
                request.prefijo(),
                request.numeroComprobante(),
                request.fechaComprobante(),
                request.fechaVencimiento(),
                request.neto(),
                request.total(),
                request.concepto(),
                request.geograficaId(),
                request.articulos() == null ? List.of() : request.articulos().stream()
                        .map(this::toArticulo)
                        .toList());
    }

    public FacturaCompraResponse toResponse(FacturaCompraResultado resultado) {
        return new FacturaCompraResponse(
                resultado.proveedorMovimientoId(),
                resultado.fechaContable(),
                resultado.ordenContable(),
                resultado.vinculo(),
                resultado.totalDebe(),
                resultado.totalHaber(),
                resultado.cuadra(),
                resultado.lineas().stream().map(asientoMapper::toLineaResponse).toList());
    }

    public FacturaCompraDetalleResponse toResponse(FacturaCompraDetalle detalle) {
        return new FacturaCompraDetalleResponse(
                detalle.proveedorMovimientoId(),
                detalle.proveedorId(),
                detalle.nombreBeneficiario(),
                detalle.comprobanteId(),
                detalle.comprobanteDescripcion(),
                detalle.prefijo(),
                detalle.numeroComprobante(),
                detalle.fechaComprobante(),
                detalle.fechaVencimiento(),
                detalle.neto(),
                detalle.importe(),
                detalle.concepto(),
                detalle.geograficaId(),
                detalle.fechaContable(),
                detalle.ordenContable(),
                detalle.articulos().stream().map(this::toResponse).toList(),
                detalle.lineas().stream().map(asientoMapper::toLineaResponse).toList());
    }

    private FacturaCompraArticulo toArticulo(FacturaCompraArticuloRequest request) {
        return new FacturaCompraArticulo(
                request.articuloId(),
                request.concepto(),
                request.cantidad(),
                request.precioUnitario(),
                request.precioFinal());
    }

    private FacturaCompraArticuloResponse toResponse(FacturaCompraArticuloDetalle articulo) {
        return new FacturaCompraArticuloResponse(
                articulo.articuloId(),
                articulo.articuloNombre(),
                articulo.concepto(),
                articulo.cantidad(),
                articulo.precioUnitario(),
                articulo.precioFinal(),
                articulo.entregado(),
                articulo.asignado());
    }

}
