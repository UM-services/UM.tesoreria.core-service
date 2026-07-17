package um.tesoreria.core.hexagonal.track.domain.ports.in;

public interface DeleteTrackUseCase {
    void deleteTrack(Long trackId);
}
