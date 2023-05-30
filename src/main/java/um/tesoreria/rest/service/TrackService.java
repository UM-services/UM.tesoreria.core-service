/**
 * 
 */
package um.tesoreria.rest.service;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.kotlin.model.Track;
import um.tesoreria.rest.repository.ITrackRepository;
import um.tesoreria.rest.repository.ITrackRepository;

/**
 * @author daniel
 *
 */
@Service
public class TrackService {
	
	@Resource
	private ITrackRepository repository;

	public Track add(Track track) {
		return repository.save(track);
	}

	@Transactional
    public void deleteByTrackId(Long trackId) {
		repository.deleteByTrackId(trackId);
    }
}
