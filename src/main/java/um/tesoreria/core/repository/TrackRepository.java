/**
 * 
 */
package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.Track;

/**
 * @author daniel
 *
 */
@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

    @Modifying
    public void deleteByTrackId(Long trackId);
}
