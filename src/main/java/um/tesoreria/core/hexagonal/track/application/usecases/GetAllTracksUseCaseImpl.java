package um.tesoreria.core.hexagonal.track.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.track.domain.model.Track;
import um.tesoreria.core.hexagonal.track.domain.ports.in.GetAllTracksUseCase;
import um.tesoreria.core.hexagonal.track.domain.ports.out.TrackRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllTracksUseCaseImpl implements GetAllTracksUseCase {
    private final TrackRepository repository;

    @Override
    public List<Track> getAllTracks() {
        return repository.findAll();
    }
}
