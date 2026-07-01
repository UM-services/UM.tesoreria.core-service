package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.entity.LectivoTotalImputacionEntity;

@Component
public class LectivoTotalImputacionMapper {

    public LectivoTotalImputacion toDomain(LectivoTotalImputacionEntity entity) {
        if (entity == null) return null;
        return LectivoTotalImputacion.builder()
                .lectivoTotalImputacionId(entity.getLectivoTotalImputacionId())
                .facultadId(entity.getFacultadId())
                .lectivoId(entity.getLectivoId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .productoId(entity.getProductoId())
                .numeroCuenta(entity.getNumeroCuenta())
                .build();
    }

    public LectivoTotalImputacionEntity toEntity(LectivoTotalImputacion domain) {
        if (domain == null) return null;
        return LectivoTotalImputacionEntity.builder()
                .lectivoTotalImputacionId(domain.getLectivoTotalImputacionId())
                .facultadId(domain.getFacultadId())
                .lectivoId(domain.getLectivoId())
                .tipoChequeraId(domain.getTipoChequeraId())
                .productoId(domain.getProductoId())
                .numeroCuenta(domain.getNumeroCuenta())
                .build();
    }

}
