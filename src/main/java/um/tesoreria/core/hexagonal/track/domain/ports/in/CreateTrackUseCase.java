package um.tesoreria.core.hexagonal.track.domain.ports.in;

import um.tesoreria.core.hexagonal.track.domain.model.Track;

public interface CreateTrackUseCase {
    Track createTrack(Track track);
}
