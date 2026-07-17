package um.tesoreria.core.hexagonal.track.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.track.domain.model.Track;
import um.tesoreria.core.hexagonal.track.domain.ports.in.GetTrackByIdUseCase;
import um.tesoreria.core.hexagonal.track.domain.ports.out.TrackRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetTrackByIdUseCaseImpl implements GetTrackByIdUseCase {
    private final TrackRepository repository;

    @Override
    public Optional<Track> getTrackById(Long trackId) {
        return repository.findById(trackId);
    }
}
