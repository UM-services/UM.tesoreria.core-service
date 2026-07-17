package um.tesoreria.core.hexagonal.track.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.track.domain.model.Track;
import um.tesoreria.core.hexagonal.track.infrastructure.web.dto.TrackRequest;
import um.tesoreria.core.hexagonal.track.infrastructure.web.dto.TrackResponse;

@Component
public class TrackDtoMapper {

    public Track toDomain(TrackRequest request) {
        if (request == null) return null;
        Track.TrackBuilder builder = Track.builder()
                .trackId(request.getTrackId());
        if (request.getDescripcion() != null) builder.descripcion(request.getDescripcion());
        return builder.build();
    }

    public TrackResponse toResponse(Track domain) {
        if (domain == null) return null;
        TrackResponse.TrackResponseBuilder builder = TrackResponse.builder()
                .trackId(domain.getTrackId());
        if (domain.getDescripcion() != null) builder.descripcion(domain.getDescripcion());
        return builder.build();
    }
}
