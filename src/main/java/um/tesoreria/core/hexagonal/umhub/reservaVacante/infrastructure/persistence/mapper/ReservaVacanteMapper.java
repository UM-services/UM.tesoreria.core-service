package um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.persistence.entity.ReservaVacanteEntity;

@Component
public class ReservaVacanteMapper {

    public ReservaVacante toDomainModel(ReservaVacanteEntity entity) {
        if (entity == null) return null;
        return ReservaVacante.builder()
                .reservaVacanteId(entity.getReservaVacanteId())
                .personaUniqueId(entity.getPersonaUniqueId())
                .campanhaId(entity.getCampanhaId())
                .estado(entity.getEstado())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .build();
    }

    public ReservaVacanteEntity toEntity(ReservaVacante domain) {
        if (domain == null) return null;
        return ReservaVacanteEntity.builder()
                .reservaVacanteId(domain.getReservaVacanteId())
                .personaUniqueId(domain.getPersonaUniqueId())
                .campanhaId(domain.getCampanhaId())
                .estado(domain.getEstado())
                .build();
    }
}
