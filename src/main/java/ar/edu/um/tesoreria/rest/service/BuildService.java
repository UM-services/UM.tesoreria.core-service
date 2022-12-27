/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.BuildNotFoundException;
import ar.edu.um.tesoreria.rest.model.Build;
import ar.edu.um.tesoreria.rest.repository.IBuildRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class BuildService {
	
	@Autowired
	private IBuildRepository repository;

	public Build findLast() {
		Build build = repository.findTopByOrderByBuildDesc().orElseThrow(() -> new BuildNotFoundException());
		log.debug("Build -> {}", build);
		return build;
	}

	public Build add(Build build) {
		repository.save(build);
		log.debug("Build -> {}", build);
		return build;
	}
	
	
}
