package um.tesoreria.core.hexagonal.track.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.track.domain.model.Track;
import um.tesoreria.core.hexagonal.track.domain.ports.out.TrackRepository;
import um.tesoreria.core.hexagonal.track.infrastructure.persistence.entity.TrackEntity;
import um.tesoreria.core.hexagonal.track.infrastructure.persistence.mapper.TrackMapper;
import um.tesoreria.core.hexagonal.track.infrastructure.persistence.repository.JpaTrackRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaTrackRepositoryAdapter implements TrackRepository {

    private final JpaTrackRepository jpaTrackRepository;
    private final TrackMapper trackMapper;

    @Override
    public List<Track> findAll() {
        return jpaTrackRepository.findAll().stream()
                .map(trackMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Track> findById(Long trackId) {
        return jpaTrackRepository.findByTrackId(trackId).map(trackMapper::toDomain);
    }

    @Override
    public Track create(Track track) {
        TrackEntity entity = trackMapper.toEntity(track);
        TrackEntity saved = jpaTrackRepository.save(entity);
        return trackMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long trackId) {
        jpaTrackRepository.deleteByTrackId(trackId);
    }
}
