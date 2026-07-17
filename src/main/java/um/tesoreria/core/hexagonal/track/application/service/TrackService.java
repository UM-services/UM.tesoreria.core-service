package um.tesoreria.core.hexagonal.track.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.track.application.exception.TrackException;
import um.tesoreria.core.hexagonal.track.domain.model.Track;
import um.tesoreria.core.hexagonal.track.domain.ports.in.*;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final CreateTrackUseCase createTrackUseCase;
    private final GetAllTracksUseCase getAllTracksUseCase;
    private final GetTrackByIdUseCase getTrackByIdUseCase;
    private final DeleteTrackUseCase deleteTrackUseCase;

    public Track createTrack(Track track) {
        return createTrackUseCase.createTrack(track);
    }

    public List<Track> findAll() {
        return getAllTracksUseCase.getAllTracks();
    }

    public Track findByTrackId(Long trackId) {
        return getTrackByIdUseCase.getTrackById(trackId).orElseThrow(() -> new TrackException(trackId));
    }

    public Optional<Track> findById(Long trackId) {
        return getTrackByIdUseCase.getTrackById(trackId);
    }

    public void deleteByTrackId(Long trackId) {
        deleteTrackUseCase.deleteTrack(trackId);
    }
}
