package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.comprobante.infrastructure.web.mapper.ComprobanteDtoMapper;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.web.mapper.ProveedorDtoMapper;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.AnulacionComprobante;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimientoDetalle;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimientoResumen;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto.AnulacionComprobanteResponse;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto.ProveedorMovimientoDetalleResponse;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto.ProveedorMovimientoRequest;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto.ProveedorMovimientoResponse;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto.ProveedorMovimientoResumenResponse;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto.ProveedorPagoAplicadoResponse;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto.ProveedorValorResponse;
import um.tesoreria.core.hexagonal.dependencias.geografica.infrastructure.web.mapper.GeograficaDtoMapper;
import um.tesoreria.core.kotlin.model.Bancaria;
import um.tesoreria.core.kotlin.model.ProveedorPago;
import um.tesoreria.core.kotlin.model.ValorMovimiento;

import java.math.BigDecimal;
import java.util.Objects;
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

    public ProveedorMovimientoResumenResponse toResumen(ProveedorMovimientoResumen domain) {
        if (domain == null) return null;
        return ProveedorMovimientoResumenResponse.builder()
                .proveedorMovimientoId(domain.getProveedorMovimientoId())
                .comprobanteId(domain.getComprobanteId())
                .comprobanteDescripcion(domain.getComprobanteDescripcion())
                .ordenPago(domain.getOrdenPago())
                .numero(domain.getNumero())
                .fechaComprobante(domain.getFechaComprobante())
                .importe(domain.getImporte())
                .cancelado(domain.getCancelado())
                .saldo(domain.getSaldo())
                .fechaAnulacion(domain.getFechaAnulacion())
                .anulable(domain.getAnulable())
                .motivosNoAnulable(domain.getMotivosNoAnulable())
                .requierePrivilegioOp(domain.getRequierePrivilegioOp())
                .build();
    }

    public ProveedorMovimientoDetalleResponse toDetalle(ProveedorMovimientoDetalle domain) {
        if (domain == null) return null;
        return ProveedorMovimientoDetalleResponse.builder()
                .proveedorMovimientoId(domain.getProveedorMovimientoId())
                .tipo(domain.getTipo())
                .cabecera(toResumen(domain.getCabecera()))
                .pagos(domain.getPagos().stream().map(this::toPagoAplicado).collect(Collectors.toList()))
                .valores(domain.getValores().stream().map(this::toValor).collect(Collectors.toList()))
                .anulacion(toAnulacion(domain.getAnulacion()))
                .build();
    }

    private AnulacionComprobanteResponse toAnulacion(AnulacionComprobante domain) {
        if (domain == null) return null;
        return AnulacionComprobanteResponse.builder()
                .habilitada(domain.getHabilitada())
                .motivos(domain.getMotivos())
                .requierePrivilegioOp(domain.getRequierePrivilegioOp())
                .build();
    }

    private ProveedorPagoAplicadoResponse toPagoAplicado(ProveedorPago proveedorPago) {
        var ordenPago = proveedorPago.getOrdenPago();
        BigDecimal importe = ordenPago != null && ordenPago.getImporte() != null
                ? ordenPago.getImporte() : BigDecimal.ZERO;
        BigDecimal cancelado = ordenPago != null && ordenPago.getCancelado() != null
                ? ordenPago.getCancelado() : BigDecimal.ZERO;
        return ProveedorPagoAplicadoResponse.builder()
                .proveedorPagoId(proveedorPago.getProveedorPagoId())
                .ordenPagoId(ordenPago != null ? ordenPago.getProveedorMovimientoId() : null)
                .comprobanteDescripcion(ordenPago != null && ordenPago.getComprobante() != null
                        ? ordenPago.getComprobante().getDescripcion() : "")
                .numero(ordenPago != null ? numero(ordenPago.getPrefijo(), ordenPago.getNumeroComprobante()) : "")
                .fechaComprobante(ordenPago != null ? ordenPago.getFechaComprobante() : null)
                .importe(importe)
                .cancelado(cancelado)
                .saldo(importe.subtract(cancelado))
                .importeAplicado(proveedorPago.getImporteAplicado())
                .build();
    }

    private ProveedorValorResponse toValor(ValorMovimiento valorMovimiento) {
        return ProveedorValorResponse.builder()
                .valorMovimientoId(valorMovimiento.getValorMovimientoId())
                .concepto(valorMovimiento.getValor() != null ? valorMovimiento.getValor().getConcepto() : "")
                .numero(valorMovimiento.getNumero())
                .fechaEmision(valorMovimiento.getFechaEmision())
                .importe(valorMovimiento.getImporte())
                .nombreTitular(valorMovimiento.getNombreTitular())
                .bancaria(bancaria(valorMovimiento.getBancaria()))
                .build();
    }

    private String bancaria(Bancaria bancaria) {
        if (bancaria == null) {
            return "";
        }
        return Objects.toString(bancaria.getBanco(), "") + " - " + Objects.toString(bancaria.getNumero(), "");
    }

    private String numero(Integer prefijo, Long numero) {
        int p = prefijo != null ? prefijo : 0;
        long n = numero != null ? numero : 0L;
        return String.format("%04d-%08d", p, n);
    }
}
