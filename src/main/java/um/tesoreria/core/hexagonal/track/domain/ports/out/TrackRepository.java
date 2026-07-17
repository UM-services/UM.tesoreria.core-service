package um.tesoreria.core.hexagonal.track.domain.ports.out;

import um.tesoreria.core.hexagonal.track.domain.model.Track;

import java.util.List;
import java.util.Optional;

public interface TrackRepository {
    List<Track> findAll();
    Optional<Track> findById(Long trackId);
    Track create(Track track);
    void deleteById(Long trackId);
}
