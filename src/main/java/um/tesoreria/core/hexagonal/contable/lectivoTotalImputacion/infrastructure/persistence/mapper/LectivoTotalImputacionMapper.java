package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.mapper.CuentaMapper;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.entity.LectivoTotalImputacionEntity;
import um.tesoreria.core.hexagonal.chequera.producto.infrastructure.persistence.mapper.ProductoMapper;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.mapper.TipoChequeraMapper;
import um.tesoreria.core.hexagonal.facultad.infrastructure.persistence.mapper.FacultadMapper;
import um.tesoreria.core.hexagonal.lectivo.infrastructure.persistence.mapper.LectivoMapper;

@Component
@RequiredArgsConstructor
public class LectivoTotalImputacionMapper {

    private final FacultadMapper facultadMapper;
    private final LectivoMapper lectivoMapper;
    private final TipoChequeraMapper tipoChequeraMapper;
    private final ProductoMapper productoMapper;
    private final CuentaMapper cuentaMapper;

    public LectivoTotalImputacion toDomain(LectivoTotalImputacionEntity entity) {
        if (entity == null) return null;
        return LectivoTotalImputacion.builder()
                .lectivoTotalImputacionId(entity.getLectivoTotalImputacionId())
                .facultadId(entity.getFacultadId())
                .lectivoId(entity.getLectivoId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .productoId(entity.getProductoId())
                .numeroCuenta(entity.getNumeroCuenta())
                .facultad(facultadMapper.toDomain(entity.getFacultad()))
                .lectivo(lectivoMapper.toDomainModel(entity.getLectivo()))
                .tipoChequera(tipoChequeraMapper.toDomainModel(entity.getTipoChequera()))
                .producto(productoMapper.toDomainModel(entity.getProducto()))
                .cuenta(cuentaMapper.toDomain(entity.getCuenta()))
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
