/**
 * 
 */
package um.tesoreria.core.service;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.Track;
import um.tesoreria.core.repository.TrackRepository;

/**
 * @author daniel
 *
 */
@Service
public class TrackService {
	
	@Resource
	private TrackRepository repository;

	public Track add(Track track) {
		return repository.save(track);
	}

	@Transactional
    public void deleteByTrackId(Long trackId) {
		repository.deleteByTrackId(trackId);
    }
}
