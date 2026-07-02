/**
 * 
 */
package um.tesoreria.core.hexagonal.chequera.arancelTipo.application.service;

import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.core.hexagonal.chequera.arancelTipo.application.exception.ArancelTipoException;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.entity.ArancelTipoEntity;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.repository.JpaArancelTipoRepository;
import um.tesoreria.core.model.view.ArancelTipoLectivo;
import um.tesoreria.core.repository.view.ArancelTipoLectivoRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ArancelTipoService {

	private final JpaArancelTipoRepository repository;
	private final ArancelTipoLectivoRepository arancelTipoLectivoRepository;

	public List<ArancelTipoEntity> findAll() {
		return repository.findAll(Sort.by("arancelTipoId").ascending());
	}

	public List<ArancelTipoEntity> findAllHabilitados() {
		return repository.findAllByHabilitado((byte) 1, Sort.by("arancelTipoId").ascending());
	}

	public List<ArancelTipoLectivo> findAllByLectivoId(Integer lectivoId) {
		Integer[] lectivoIds = { lectivoId, lectivoId - 1 };
		return arancelTipoLectivoRepository.findAllByLectivoIdIn(Arrays.asList(lectivoIds));
	}

	public ArancelTipoEntity findByArancelTipoId(Integer arancelTipoId) {
		return repository.findByArancelTipoId(arancelTipoId)
				.orElseThrow(() -> new ArancelTipoException(arancelTipoId));
	}

	public ArancelTipoEntity findByArancelTipoIdCompleto(Integer arancelTipoIdCompleto) {
		return repository.findByArancelTipoIdCompleto(arancelTipoIdCompleto)
				.orElseThrow(() -> new ArancelTipoException(arancelTipoIdCompleto));
	}

	public ArancelTipoEntity findLast() {
		return repository.findTopByOrderByArancelTipoIdDesc().orElse(new ArancelTipoEntity());
	}

	public void deleteByArancelTipoId(Integer arancelTipoId) {
		repository.deleteByArancelTipoId(arancelTipoId);
	}

	public ArancelTipoEntity add(ArancelTipoEntity arancelTipo) {
		arancelTipo = repository.save(arancelTipo);
		log.debug("ArancelTipo added -> " + arancelTipo.jsonify());
		return arancelTipo;
	}

	public ArancelTipoEntity update(ArancelTipoEntity newArancelTipo, Integer arancelTipoId) {
		log.debug("ArancelTipo new -> {}", newArancelTipo.jsonify());
		return repository.findByArancelTipoId(arancelTipoId).map(arancelTipo -> {
			arancelTipo.setDescripcion(newArancelTipo.getDescripcion());
			arancelTipo.setMedioArancel(newArancelTipo.getMedioArancel());
			arancelTipo.setArancelTipoIdCompleto(newArancelTipo.getArancelTipoIdCompleto());
			arancelTipo.setHabilitado(newArancelTipo.getHabilitado());
			arancelTipo = repository.save(arancelTipo);
			log.debug("ArancelTipo updated -> " + arancelTipo.jsonify());
			return arancelTipo;
		}).orElseThrow(() -> new ArancelTipoException(arancelTipoId));
	}

}
