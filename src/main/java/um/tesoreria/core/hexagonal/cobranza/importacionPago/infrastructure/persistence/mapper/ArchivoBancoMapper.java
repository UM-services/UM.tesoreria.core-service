package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoBanco;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.entity.ArchivoBancoEntity;

@Component
public class ArchivoBancoMapper {

    public ArchivoBancoEntity toEntity(ArchivoBanco domain) {
        if (domain == null) {
            return null;
        }
        return ArchivoBancoEntity.builder()
                .id(domain.getId())
                .codigoEmpresa(domain.getCodigoEmpresa())
                .identificacion(domain.getIdentificacion())
                .productoId(domain.getProductoId())
                .mesPago(domain.getMesPago())
                .anhoPago(domain.getAnhoPago())
                .cuotaId(domain.getCuotaId())
                .importe(domain.getImporte())
                .fechaPago(domain.getFechaPago())
                .marcaAcreditacion(domain.getMarcaAcreditacion())
                .fechaAcreditacion(domain.getFechaAcreditacion())
                .verificador(domain.getVerificador())
                .path(domain.getPath())
                .archivo(domain.getArchivo())
                .pasado(domain.getPasado())
                .tipoPagoId(domain.getTipoPagoId())
                .repetido(domain.getRepetido())
                .build();
    }

    public ArchivoBanco toDomain(ArchivoBancoEntity entity) {
        if (entity == null) {
            return null;
        }
        return ArchivoBanco.builder()
                .id(entity.getId())
                .codigoEmpresa(entity.getCodigoEmpresa())
                .identificacion(entity.getIdentificacion())
                .productoId(entity.getProductoId())
                .mesPago(entity.getMesPago())
                .anhoPago(entity.getAnhoPago())
                .cuotaId(entity.getCuotaId())
                .importe(entity.getImporte())
                .fechaPago(entity.getFechaPago())
                .marcaAcreditacion(entity.getMarcaAcreditacion())
                .fechaAcreditacion(entity.getFechaAcreditacion())
                .verificador(entity.getVerificador())
                .path(entity.getPath())
                .archivo(entity.getArchivo())
                .cuando(entity.getCuando())
                .pasado(entity.getPasado())
                .tipoPagoId(entity.getTipoPagoId())
                .repetido(entity.getRepetido())
                .build();
    }

}
