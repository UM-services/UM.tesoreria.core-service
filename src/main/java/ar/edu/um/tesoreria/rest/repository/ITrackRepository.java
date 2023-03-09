/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.kotlin.Track;

/**
 * @author daniel
 *
 */
@Repository
public interface ITrackRepository extends JpaRepository<Track, Long> {

    @Modifying
    public void deleteByTrackId(Long trackId);
}
