/**
 * 
 */
package um.tesoreria.rest.service;

import um.tesoreria.rest.kotlin.model.Build;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.exception.BuildException;
import um.tesoreria.rest.repository.IBuildRepository;
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
		Build build = repository.findTopByOrderByBuildDesc().orElseThrow(() -> new BuildException());
		log.debug("Build -> {}", build);
		return build;
	}

	public Build add(Build build) {
		repository.save(build);
		log.debug("Build -> {}", build);
		return build;
	}
	
	
}
