package um.tesoreria.core.hexagonal.track.domain.ports.in;

import um.tesoreria.core.hexagonal.track.domain.model.Track;

import java.util.Optional;

public interface GetTrackByIdUseCase {
    Optional<Track> getTrackById(Long trackId);
}
