package um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.web.dto.ChequeraPagoRequest;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.web.dto.ChequeraPagoResponse;

@Component
public class ChequeraPagoDtoMapper {

    public ChequeraPago toDomain(ChequeraPagoRequest request) {
        if (request == null) return null;
        var builder = ChequeraPago.builder()
                .facultadId(request.getFacultadId())
                .tipoChequeraId(request.getTipoChequeraId())
                .chequeraSerieId(request.getChequeraSerieId())
                .productoId(request.getProductoId())
                .alternativaId(request.getAlternativaId())
                .cuotaId(request.getCuotaId())
                .orden(request.getOrden())
                .fecha(request.getFecha())
                .acreditacion(request.getAcreditacion())
                .archivoBancoId(request.getArchivoBancoId())
                .archivoBancoIdAcreditacion(request.getArchivoBancoIdAcreditacion())
                .tipoPagoId(request.getTipoPagoId())
                .idMercadoPago(request.getIdMercadoPago());
        if (request.getMes() != null) builder.mes(request.getMes());
        if (request.getAnho() != null) builder.anho(request.getAnho());
        if (request.getImporte() != null) builder.importe(request.getImporte());
        if (request.getPath() != null) builder.path(request.getPath());
        if (request.getArchivo() != null) builder.archivo(request.getArchivo());
        if (request.getObservaciones() != null) builder.observaciones(request.getObservaciones());
        if (request.getVerificador() != null) builder.verificador(request.getVerificador());
        return builder.build();
    }

    public ChequeraPagoResponse toResponse(ChequeraPago domain) {
        if (domain == null) return null;
        var builder = ChequeraPagoResponse.builder()
                .chequeraPagoId(domain.getChequeraPagoId())
                .chequeraCuotaId(domain.getChequeraCuotaId())
                .facultadId(domain.getFacultadId())
                .tipoChequeraId(domain.getTipoChequeraId())
                .chequeraSerieId(domain.getChequeraSerieId())
                .productoId(domain.getProductoId())
                .alternativaId(domain.getAlternativaId())
                .cuotaId(domain.getCuotaId())
                .orden(domain.getOrden())
                .fecha(domain.getFecha())
                .acreditacion(domain.getAcreditacion())
                .archivoBancoId(domain.getArchivoBancoId())
                .archivoBancoIdAcreditacion(domain.getArchivoBancoIdAcreditacion())
                .tipoPagoId(domain.getTipoPagoId())
                .idMercadoPago(domain.getIdMercadoPago());
        if (domain.getMes() != null) builder.mes(domain.getMes());
        if (domain.getAnho() != null) builder.anho(domain.getAnho());
        if (domain.getImporte() != null) builder.importe(domain.getImporte());
        if (domain.getPath() != null) builder.path(domain.getPath());
        if (domain.getArchivo() != null) builder.archivo(domain.getArchivo());
        if (domain.getObservaciones() != null) builder.observaciones(domain.getObservaciones());
        if (domain.getVerificador() != null) builder.verificador(domain.getVerificador());
        builder.tipoPago(domain.getTipoPago())
                .producto(domain.getProducto())
                .chequeraCuota(domain.getChequeraCuota());
        return builder.build();
    }

}
