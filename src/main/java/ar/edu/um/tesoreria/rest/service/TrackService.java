/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.kotlin.Track;
import ar.edu.um.tesoreria.rest.repository.ITrackRepository;

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
