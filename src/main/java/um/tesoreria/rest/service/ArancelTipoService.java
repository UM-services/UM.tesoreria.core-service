/**
 * 
 */
package um.tesoreria.rest.service;

import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.exception.ArancelTipoException;
import um.tesoreria.rest.kotlin.model.ArancelTipo;
import um.tesoreria.rest.model.view.ArancelTipoLectivo;
import um.tesoreria.rest.repository.IArancelTipoRepository;
import um.tesoreria.rest.repository.view.IArancelTipoLectivoRepository;
import lombok.extern.slf4j.Slf4j;
import um.tesoreria.rest.exception.ArancelTipoException;
import um.tesoreria.rest.repository.IArancelTipoRepository;
import um.tesoreria.rest.repository.view.IArancelTipoLectivoRepository;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class ArancelTipoService {

	@Resource
	private IArancelTipoRepository repository;

	@Resource
	private IArancelTipoLectivoRepository arancelTipoLectivoRepository;

	public List<ArancelTipo> findAll() {
		return repository.findAll(Sort.by("arancelTipoId").ascending());
	}

	public List<ArancelTipoLectivo> findAllByLectivoId(Integer lectivoId) {
		Integer[] lectivoIds = { lectivoId, lectivoId - 1 };
		return arancelTipoLectivoRepository.findAllByLectivoIdIn(Arrays.asList(lectivoIds));
	}

	public ArancelTipo findByArancelTipoId(Integer arancelTipoId) {
		return repository.findByArancelTipoId(arancelTipoId)
				.orElseThrow(() -> new ArancelTipoException(arancelTipoId));
	}

	public ArancelTipo findByArancelTipoIdCompleto(Integer arancelTipoIdCompleto) {
		return repository.findByArancelTipoIdCompleto(arancelTipoIdCompleto)
				.orElseThrow(() -> new ArancelTipoException(arancelTipoIdCompleto));
	}

	public ArancelTipo findLast() {
		return repository.findTopByOrderByArancelTipoIdDesc().orElse(new ArancelTipo());
	}

	public void deleteByArancelTipoId(Integer arancelTipoId) {
		repository.deleteByArancelTipoId(arancelTipoId);
	}

	public ArancelTipo add(ArancelTipo arancelTipo) {
		arancelTipo = repository.save(arancelTipo);
		log.debug("ArancelTipo -> " + arancelTipo);
		return arancelTipo;
	}

	public ArancelTipo update(ArancelTipo newArancelTipo, Integer arancelTipoId) {
		return repository.findByArancelTipoId(arancelTipoId).map(arancelTipo -> {
			arancelTipo = new ArancelTipo(newArancelTipo.getArancelTipoId(), newArancelTipo.getDescripcion(),
					newArancelTipo.getMedioArancel(), newArancelTipo.getArancelTipoIdCompleto());
			repository.save(arancelTipo);
			log.debug("ArancelTipo -> " + arancelTipo);
			return arancelTipo;
		}).orElseThrow(() -> new ArancelTipoException(arancelTipoId));
	}

}
