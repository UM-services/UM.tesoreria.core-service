package um.tesoreria.core.hexagonal.track.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.track.domain.model.Track;
import um.tesoreria.core.hexagonal.track.infrastructure.persistence.entity.TrackEntity;

@Component
public class TrackMapper {

    public Track toDomain(TrackEntity entity) {
        if (entity == null) return null;
        Track.TrackBuilder builder = Track.builder()
                .trackId(entity.getTrackId());
        if (entity.getDescripcion() != null) builder.descripcion(entity.getDescripcion());
        return builder.build();
    }

    public TrackEntity toEntity(Track domain) {
        if (domain == null) return null;
        TrackEntity.TrackEntityBuilder builder = TrackEntity.builder()
                .trackId(domain.getTrackId());
        if (domain.getDescripcion() != null) builder.descripcion(domain.getDescripcion());
        return builder.build();
    }
}
