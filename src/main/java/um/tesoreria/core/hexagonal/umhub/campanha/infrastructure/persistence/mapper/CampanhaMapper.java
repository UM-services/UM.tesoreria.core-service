package um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.model.Campanha;
import um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.persistence.entity.CampanhaEntity;

@Component
public class CampanhaMapper {

    public Campanha toDomainModel(CampanhaEntity entity) {
        if (entity == null) return null;
        return Campanha.builder()
                .campanhaId(entity.getCampanhaId())
                .nombre(entity.getNombre())
                .activa(entity.getActiva())
                .valorReserva(entity.getValorReserva())
                .created(entity.getCreated())
                .build();
    }

    public CampanhaEntity toEntity(Campanha domain) {
        if (domain == null) return null;
        CampanhaEntity entity = CampanhaEntity.builder()
                .campanhaId(domain.getCampanhaId())
                .nombre(domain.getNombre())
                .activa(domain.getActiva())
                .valorReserva(domain.getValorReserva())
                .build();
        entity.setCreated(domain.getCreated());
        return entity;
    }
}
