package um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.persistence.entity.ChequeraPagoEntity;

@Component
public class ChequeraPagoMapper {

    public ChequeraPago toDomain(ChequeraPagoEntity entity) {
        if (entity == null) return null;
        var builder = ChequeraPago.builder()
                .chequeraPagoId(entity.getChequeraPagoId())
                .chequeraCuotaId(entity.getChequeraCuotaId())
                .facultadId(entity.getFacultadId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .chequeraSerieId(entity.getChequeraSerieId())
                .productoId(entity.getProductoId())
                .alternativaId(entity.getAlternativaId())
                .cuotaId(entity.getCuotaId())
                .orden(entity.getOrden())
                .fecha(entity.getFecha())
                .acreditacion(entity.getAcreditacion())
                .archivoBancoId(entity.getArchivoBancoId())
                .archivoBancoIdAcreditacion(entity.getArchivoBancoIdAcreditacion())
                .tipoPagoId(entity.getTipoPagoId())
                .idMercadoPago(entity.getIdMercadoPago());
        if (entity.getMes() != null) builder.mes(entity.getMes());
        if (entity.getAnho() != null) builder.anho(entity.getAnho());
        if (entity.getImporte() != null) builder.importe(entity.getImporte());
        if (entity.getPath() != null) builder.path(entity.getPath());
        if (entity.getArchivo() != null) builder.archivo(entity.getArchivo());
        if (entity.getObservaciones() != null) builder.observaciones(entity.getObservaciones());
        if (entity.getVerificador() != null) builder.verificador(entity.getVerificador());
        return builder.build();
    }

    public ChequeraPagoEntity toEntity(ChequeraPago domain) {
        if (domain == null) return null;
        return ChequeraPagoEntity.builder()
                .chequeraPagoId(domain.getChequeraPagoId())
                .chequeraCuotaId(domain.getChequeraCuotaId())
                .facultadId(domain.getFacultadId())
                .tipoChequeraId(domain.getTipoChequeraId())
                .chequeraSerieId(domain.getChequeraSerieId())
                .productoId(domain.getProductoId())
                .alternativaId(domain.getAlternativaId())
                .cuotaId(domain.getCuotaId())
                .orden(domain.getOrden())
                .mes(domain.getMes())
                .anho(domain.getAnho())
                .fecha(domain.getFecha())
                .acreditacion(domain.getAcreditacion())
                .importe(domain.getImporte())
                .path(domain.getPath())
                .archivo(domain.getArchivo())
                .observaciones(domain.getObservaciones())
                .archivoBancoId(domain.getArchivoBancoId())
                .archivoBancoIdAcreditacion(domain.getArchivoBancoIdAcreditacion())
                .verificador(domain.getVerificador())
                .tipoPagoId(domain.getTipoPagoId())
                .idMercadoPago(domain.getIdMercadoPago())
                .build();
    }

}
