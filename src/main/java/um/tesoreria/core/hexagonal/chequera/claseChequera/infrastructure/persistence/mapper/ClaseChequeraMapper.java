package um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.persistence.entity.ClaseChequeraEntity;

@Component
public class ClaseChequeraMapper {

    public ClaseChequera toDomainModel(ClaseChequeraEntity entity) {
        if (entity == null) return null;
        return ClaseChequera.builder()
                .claseChequeraId(entity.getClaseChequeraId())
                .nombre(entity.getNombre())
                .preuniversitario(entity.getPreuniversitario())
                .grado(entity.getGrado())
                .posgrado(entity.getPosgrado())
                .curso(entity.getCurso())
                .secundario(entity.getSecundario())
                .titulo(entity.getTitulo())
                .build();
    }

    public ClaseChequeraEntity toEntity(ClaseChequera domain) {
        if (domain == null) return null;
        return ClaseChequeraEntity.builder()
                .claseChequeraId(domain.getClaseChequeraId())
                .nombre(domain.getNombre())
                .preuniversitario(domain.getPreuniversitario())
                .grado(domain.getGrado())
                .posgrado(domain.getPosgrado())
                .curso(domain.getCurso())
                .secundario(domain.getSecundario())
                .titulo(domain.getTitulo())
                .build();
    }

}
