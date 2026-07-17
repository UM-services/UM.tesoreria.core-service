package um.tesoreria.core.hexagonal.track.domain.ports.in;

import um.tesoreria.core.hexagonal.track.domain.model.Track;

import java.util.List;

public interface GetAllTracksUseCase {
    List<Track> getAllTracks();
}
