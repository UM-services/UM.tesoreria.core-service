package um.tesoreria.core.hexagonal.chequera.lectivoCuota.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.infrastructure.persistence.entity.LectivoCuotaEntity;

@Component
public class LectivoCuotaMapper {

    public LectivoCuota toDomain(LectivoCuotaEntity entity) {
        if (entity == null) return null;
        return LectivoCuota.builder()
                .lectivoCuotaId(entity.getLectivoCuotaId())
                .facultadId(entity.getFacultadId())
                .lectivoId(entity.getLectivoId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .productoId(entity.getProductoId())
                .alternativaId(entity.getAlternativaId())
                .cuotaId(entity.getCuotaId())
                .mes(entity.getMes())
                .anho(entity.getAnho())
                .vencimiento1(entity.getVencimiento1())
                .importe1(entity.getImporte1())
                .vencimiento2(entity.getVencimiento2())
                .importe2(entity.getImporte2())
                .vencimiento3(entity.getVencimiento3())
                .importe3(entity.getImporte3())
                .tramoId(entity.getTramoId())
                .build();
    }

    public LectivoCuotaEntity toEntity(LectivoCuota domain) {
        if (domain == null) return null;
        return LectivoCuotaEntity.builder()
                .lectivoCuotaId(domain.getLectivoCuotaId())
                .facultadId(domain.getFacultadId())
                .lectivoId(domain.getLectivoId())
                .tipoChequeraId(domain.getTipoChequeraId())
                .productoId(domain.getProductoId())
                .alternativaId(domain.getAlternativaId())
                .cuotaId(domain.getCuotaId())
                .mes(domain.getMes())
                .anho(domain.getAnho())
                .vencimiento1(domain.getVencimiento1())
                .importe1(domain.getImporte1())
                .vencimiento2(domain.getVencimiento2())
                .importe2(domain.getImporte2())
                .vencimiento3(domain.getVencimiento3())
                .importe3(domain.getImporte3())
                .tramoId(domain.getTramoId())
                .build();
    }
}
