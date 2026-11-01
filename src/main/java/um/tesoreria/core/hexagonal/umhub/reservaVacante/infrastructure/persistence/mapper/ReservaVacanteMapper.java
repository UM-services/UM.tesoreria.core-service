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
                .importe(entity.getImporte())
                .vencimiento(entity.getVencimiento())
                .notificacionPago(entity.getNotificacionPago())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .build();
    }

    public ReservaVacanteEntity toEntity(ReservaVacante domain) {
        if (domain == null) return null;
        var builder = ReservaVacanteEntity.builder()
                .reservaVacanteId(domain.getReservaVacanteId())
                .personaUniqueId(domain.getPersonaUniqueId())
                .campanhaId(domain.getCampanhaId())
                .estado(domain.getEstado())
                .importe(domain.getImporte())
                .vencimiento(domain.getVencimiento());
        // En el alta el flag no viene del dominio: se deja el default (false) de la entidad.
        if (domain.getNotificacionPago() != null) {
            builder.notificacionPago(domain.getNotificacionPago());
        }
        return builder.build();
    }
}
