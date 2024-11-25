/**
 * 
 */
package um.tesoreria.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import um.tesoreria.core.kotlin.model.Build;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.BuildException;
import um.tesoreria.core.repository.IBuildRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class BuildService {
	
	private final IBuildRepository repository;

	public BuildService(IBuildRepository repository) {
		this.repository = repository;
	}

	public Build findLast() {
		Build build = repository.findTopByOrderByBuildDesc().orElseThrow(BuildException::new);
		logBuild(build);
		return build;
	}

	public Build add(Build build) {
		build = repository.save(build);
		logBuild(build);
		return build;
	}

	private void logBuild(Build build) {
        try {
            log.debug("build = {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(build));
        } catch (JsonProcessingException e) {
            log.debug("build error = {}", e.getMessage());
        }
    }


}
