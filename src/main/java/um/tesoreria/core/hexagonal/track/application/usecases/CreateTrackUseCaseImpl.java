package um.tesoreria.core.hexagonal.track.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.track.domain.model.Track;
import um.tesoreria.core.hexagonal.track.domain.ports.in.CreateTrackUseCase;
import um.tesoreria.core.hexagonal.track.domain.ports.out.TrackRepository;

@Component
@RequiredArgsConstructor
public class CreateTrackUseCaseImpl implements CreateTrackUseCase {
    private final TrackRepository repository;

    @Override
    public Track createTrack(Track track) {
        return repository.create(track);
    }
}
