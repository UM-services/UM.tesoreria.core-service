package um.tesoreria.core.hexagonal.lectivo.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.lectivo.infrastructure.persistence.entity.LectivoEntity;

@Component
public class LectivoMapper {

    public Lectivo toDomainModel(LectivoEntity entity) {
        if (entity == null) return null;
        return Lectivo.builder()
                .lectivoId(entity.getLectivoId())
                .nombre(entity.getNombre())
                .fechaInicio(entity.getFechaInicio())
                .fechaFinal(entity.getFechaFinal())
                .build();
    }

    public LectivoEntity toEntity(Lectivo domain) {
        if (domain == null) return null;
        LectivoEntity.LectivoEntityBuilder builder = LectivoEntity.builder()
                .lectivoId(domain.getLectivoId())
                .fechaInicio(domain.getFechaInicio())
                .fechaFinal(domain.getFechaFinal());
        
        if (domain.getNombre() != null) builder.nombre(domain.getNombre());
        
        return builder.build();
    }
}
