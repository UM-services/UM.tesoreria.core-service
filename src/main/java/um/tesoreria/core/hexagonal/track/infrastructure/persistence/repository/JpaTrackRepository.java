/**
 * 
 */
package um.tesoreria.core.hexagonal.track.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.track.infrastructure.persistence.entity.TrackEntity;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaTrackRepository extends JpaRepository<TrackEntity, Long> {

    Optional<TrackEntity> findByTrackId(Long trackId);

    @Modifying
    void deleteByTrackId(Long trackId);
}
