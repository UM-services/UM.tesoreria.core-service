package um.tesoreria.core.hexagonal.geografica.infrastructure.persistence.mapper;

import um.tesoreria.core.hexagonal.geografica.domain.model.Geografica;
import um.tesoreria.core.hexagonal.geografica.infrastructure.persistence.entity.GeograficaEntity;
import org.springframework.stereotype.Component;

@Component
public class GeograficaMapper {

    public Geografica toDomainModel(GeograficaEntity entity) {
        if (entity == null) return null;
        return new Geografica(
                entity.getGeograficaId(),
                entity.getNombre(),
                entity.getSinChequera()
        );
    }

    public GeograficaEntity toEntity(Geografica domain) {
        if (domain == null) return null;
        GeograficaEntity entity = new GeograficaEntity();
        entity.setGeograficaId(domain.getGeograficaId());
        entity.setNombre(domain.getNombre());
        entity.setSinChequera(domain.getSinChequera());
        return entity;
    }

}
