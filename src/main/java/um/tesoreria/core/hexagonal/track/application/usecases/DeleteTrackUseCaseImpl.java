package um.tesoreria.core.hexagonal.track.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.track.domain.ports.in.DeleteTrackUseCase;
import um.tesoreria.core.hexagonal.track.domain.ports.out.TrackRepository;

@Component
@RequiredArgsConstructor
public class DeleteTrackUseCaseImpl implements DeleteTrackUseCase {
    private final TrackRepository repository;

    @Override
    public void deleteTrack(Long trackId) {
        repository.deleteById(trackId);
    }
}
