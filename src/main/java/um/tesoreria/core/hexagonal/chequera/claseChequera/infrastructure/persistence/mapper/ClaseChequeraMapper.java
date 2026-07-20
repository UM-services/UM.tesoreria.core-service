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
                .tramite(entity.getTramite())
                .build();
    }

    public ClaseChequeraEntity toEntity(ClaseChequera domain) {
        if (domain == null) return null;
        ClaseChequeraEntity.ClaseChequeraEntityBuilder builder = ClaseChequeraEntity.builder()
                .claseChequeraId(domain.getClaseChequeraId())
                .nombre(domain.getNombre());
        
        if (domain.getPreuniversitario() != null) builder.preuniversitario(domain.getPreuniversitario());
        if (domain.getGrado() != null) builder.grado(domain.getGrado());
        if (domain.getPosgrado() != null) builder.posgrado(domain.getPosgrado());
        if (domain.getCurso() != null) builder.curso(domain.getCurso());
        if (domain.getSecundario() != null) builder.secundario(domain.getSecundario());
        if (domain.getTitulo() != null) builder.titulo(domain.getTitulo());
        if (domain.getTramite() != null) builder.tramite(domain.getTramite());
        
        return builder.build();
    }

}
