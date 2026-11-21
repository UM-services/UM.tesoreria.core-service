package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.AsientoPropuesto;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.LineaPropuesta;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoDetalle;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoDirectaGasto;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoDirectaSolicitud;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoFactura;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoPreview;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoResultado;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoSolicitud;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoValor;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.AsientoLineaResponse;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.AsientoOrigenResponse;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.AsientoPropuestoResponse;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.OrdenPagoDetalleFacturaResponse;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.OrdenPagoDetalleLineaResponse;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.OrdenPagoDetalleResponse;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.OrdenPagoDetalleValorResponse;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.OrdenPagoDirectaRequest;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.OrdenPagoPreviewResponse;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.OrdenPagoRequest;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.OrdenPagoResponse;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.OrdenPagoValorRequest;

import java.util.List;

@Component
public class OrdenPagoDtoMapper {

    public OrdenPagoSolicitud toDomain(OrdenPagoRequest request) {
        if (request == null) {
            return null;
        }
        return new OrdenPagoSolicitud(request.proveedorId(),
                request.comprobanteId(),
                request.fechaComprobante(),
                request.fechaVencimiento(),
                request.importe(),
                request.concepto(),
                request.conCargo(),
                request.solicitaFactura(),
                request.geograficaId(),
                request.numeroManual(),
                request.notificar(),
                request.emailProveedor(),
                request.emailInterno(),
                request.facturas() == null ? List.of() : request.facturas().stream()
                        .map(factura -> new OrdenPagoFactura(factura.proveedorMovimientoId(), factura.pago()))
                        .toList(),
                toValores(request.valores()));
    }

    public OrdenPagoDirectaSolicitud toDomain(OrdenPagoDirectaRequest request) {
        if (request == null) {
            return null;
        }
        return new OrdenPagoDirectaSolicitud(request.nombreBeneficiario(),
                request.fechaComprobante(),
                request.fechaVencimiento(),
                request.importe(),
                request.concepto(),
                request.conCargo(),
                request.solicitaFactura(),
                request.geograficaId(),
                request.numeroManual(),
                request.notificar(),
                request.gastos() == null ? List.of() : request.gastos().stream()
                        .map(gasto -> new OrdenPagoDirectaGasto(gasto.numeroCuenta(), gasto.importe()))
                        .toList(),
                toValores(request.valores()));
    }

    private List<OrdenPagoValor> toValores(List<OrdenPagoValorRequest> valores) {
        if (valores == null) {
            return List.of();
        }
        return valores.stream()
                .map(valor -> new OrdenPagoValor(valor.valorId(),
                        valor.numero(),
                        valor.fechaEmision(),
                        valor.fechaEfectivizacion(),
                        valor.importe(),
                        valor.bancariaIdOrigen(),
                        valor.nombreTitular(),
                        valor.cuitTitular(),
                        valor.letras()))
                .toList();
    }

    public OrdenPagoPreviewResponse toResponse(OrdenPagoPreview preview) {
        if (preview == null) {
            return null;
        }
        return new OrdenPagoPreviewResponse(toResponse(preview.asientos()),
                preview.totalDebe(),
                preview.totalHaber(),
                preview.cuadra(),
                preview.totalFacturas(),
                preview.totalValores(),
                preview.prefijo(),
                preview.numeroComprobante(),
                preview.errores());
    }

    public OrdenPagoResponse toResponse(OrdenPagoResultado resultado) {
        if (resultado == null) {
            return null;
        }
        return new OrdenPagoResponse(resultado.proveedorMovimientoId(),
                resultado.prefijo(),
                resultado.numeroComprobante(),
                resultado.importe(),
                resultado.cancelado(),
                resultado.valorMovimientoIds(),
                toResponse(resultado.asientos()));
    }

    public OrdenPagoDetalleResponse toResponse(OrdenPagoDetalle detalle) {
        if (detalle == null) {
            return null;
        }
        return new OrdenPagoDetalleResponse(detalle.proveedorMovimientoId(),
                detalle.proveedorId(),
                detalle.nombreBeneficiario(),
                detalle.comprobanteId(),
                detalle.prefijo(),
                detalle.numeroComprobante(),
                detalle.fechaComprobante(),
                detalle.fechaVencimiento(),
                detalle.importe(),
                detalle.cancelado(),
                detalle.concepto(),
                detalle.conCargo(),
                detalle.solicitaFactura(),
                detalle.geograficaId(),
                detalle.fechaContable(),
                detalle.ordenContable(),
                detalle.facturas().stream()
                        .map(factura -> new OrdenPagoDetalleFacturaResponse(factura.proveedorMovimientoId(),
                                factura.comprobanteId(),
                                factura.prefijo(),
                                factura.numeroComprobante(),
                                factura.importe(),
                                factura.cancelado(),
                                factura.importeAplicado(),
                                factura.trazaContable(),
                                factura.fechaContable(),
                                factura.ordenContable()))
                        .toList(),
                detalle.valores().stream()
                        .map(valor -> new OrdenPagoDetalleValorResponse(valor.valorMovimientoId(),
                                valor.valorId(),
                                valor.concepto(),
                                valor.numero(),
                                valor.fechaEmision(),
                                valor.importe(),
                                valor.estado(),
                                valor.fechaContable(),
                                valor.ordenContable()))
                        .toList(),
                detalle.traza().stream()
                        .map(linea -> new OrdenPagoDetalleLineaResponse(linea.fechaContable(),
                                linea.ordenContable(),
                                linea.item(),
                                linea.numeroCuenta(),
                                linea.debita(),
                                linea.importe(),
                                linea.comprobanteId(),
                                linea.concepto()))
                        .toList());
    }

    private List<AsientoPropuestoResponse> toResponse(List<AsientoPropuesto> asientos) {
        return asientos.stream().map(this::toResponse).toList();
    }

    private AsientoPropuestoResponse toResponse(AsientoPropuesto asiento) {
        return new AsientoPropuestoResponse(asiento.fechaContable(),
                asiento.ordenContable(),
                asiento.origenes().stream()
                        .map(origen -> new AsientoOrigenResponse(origen.tipo().name(), origen.referenciaId()))
                        .toList(),
                asiento.lineas().stream().map(this::toResponse).toList(),
                asiento.totalDebe(),
                asiento.totalHaber(),
                asiento.cuadra());
    }

    private AsientoLineaResponse toResponse(LineaPropuesta linea) {
        return new AsientoLineaResponse(linea.numeroCuenta(),
                linea.debita(),
                linea.importe(),
                linea.comprobanteId(),
                linea.proveedorId(),
                linea.concepto());
    }
}
