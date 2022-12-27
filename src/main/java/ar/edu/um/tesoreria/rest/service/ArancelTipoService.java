/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ArancelTipoNotFoundException;
import ar.edu.um.tesoreria.rest.model.ArancelTipo;
import ar.edu.um.tesoreria.rest.model.view.ArancelTipoLectivo;
import ar.edu.um.tesoreria.rest.repository.IArancelTipoRepository;
import ar.edu.um.tesoreria.rest.repository.view.IArancelTipoLectivoRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class ArancelTipoService {

	@Autowired
	private IArancelTipoRepository repository;

	@Autowired
	private IArancelTipoLectivoRepository arancelTipoLectivoRepository;

	public List<ArancelTipo> findAll() {
		return repository.findAll(Sort.by("arancelTipoId").ascending());
	}

	public List<ArancelTipoLectivo> findAllByLectivoId(Integer lectivoId) {
		Integer[] lectivos = { lectivoId, lectivoId - 1 };
		return arancelTipoLectivoRepository.findAllByLectivoIdIn(Arrays.asList(lectivos));
	}

	public ArancelTipo findByArancelTipoId(Integer arancelTipoId) {
		return repository.findByArancelTipoId(arancelTipoId)
				.orElseThrow(() -> new ArancelTipoNotFoundException(arancelTipoId));
	}

	public ArancelTipo findByArancelTipoIdCompleto(Integer arancelTipoIdCompleto) {
		return repository.findByArancelTipoIdCompleto(arancelTipoIdCompleto)
				.orElseThrow(() -> new ArancelTipoNotFoundException(arancelTipoIdCompleto));
	}

	public ArancelTipo findLast() {
		return repository.findTopByOrderByArancelTipoIdDesc().orElse(new ArancelTipo());
	}

	public void deleteByArancelTipoId(Integer arancelTipoId) {
		repository.deleteByArancelTipoId(arancelTipoId);
	}

	public ArancelTipo add(ArancelTipo arancelTipo) {
		arancelTipo = repository.save(arancelTipo);
		log.debug("ArancelTipo -> " + arancelTipo.toString());
		return arancelTipo;
	}

	public ArancelTipo update(ArancelTipo newArancelTipo, Integer arancelTipoId) {
		return repository.findByArancelTipoId(arancelTipoId).map(arancelTipo -> {
			arancelTipo = new ArancelTipo(newArancelTipo.getArancelTipoId(), newArancelTipo.getDescripcion(),
					newArancelTipo.getMedioArancel(), newArancelTipo.getArancelTipoIdCompleto());
			repository.save(arancelTipo);
			log.debug("ArancelTipo -> " + arancelTipo.toString());
			return arancelTipo;
		}).orElseThrow(() -> new ArancelTipoNotFoundException(arancelTipoId));
	}

}
