package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.persistence.mapper.ClaseChequeraMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model.UsuarioChequeraClaseChequera;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.entity.UsuarioChequeraClaseChequeraEntity;

@Component
@RequiredArgsConstructor
public class UsuarioChequeraClaseChequeraMapper {

    private final ClaseChequeraMapper claseChequeraMapper;

    public UsuarioChequeraClaseChequera toDomain(UsuarioChequeraClaseChequeraEntity entity) {
        if (entity == null) return null;
        return UsuarioChequeraClaseChequera.builder()
                .usuarioChequeraClaseChequeraId(entity.getUsuarioChequeraClaseChequeraId())
                .userId(entity.getUserId())
                .claseChequeraId(entity.getClaseChequeraId())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .claseChequera(claseChequeraMapper.toDomainModel(entity.getClaseChequera()))
                .build();
    }

    public UsuarioChequeraClaseChequeraEntity toEntity(UsuarioChequeraClaseChequera domain) {
        if (domain == null) return null;
        return UsuarioChequeraClaseChequeraEntity.builder()
                .usuarioChequeraClaseChequeraId(domain.getUsuarioChequeraClaseChequeraId())
                .userId(domain.getUserId())
                .claseChequeraId(domain.getClaseChequeraId())
                .build();
    }
}
