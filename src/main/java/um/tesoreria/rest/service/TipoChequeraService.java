/**
 * 
 */
package um.tesoreria.rest.service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.exception.TipoChequeraException;
import um.tesoreria.rest.repository.ITipoChequeraRepository;
import um.tesoreria.rest.exception.TipoChequeraException;
import um.tesoreria.rest.kotlin.model.TipoChequera;
import um.tesoreria.rest.repository.ITipoChequeraRepository;

/**
 * @author daniel
 *
 */
@Service
public class TipoChequeraService {

	@Autowired
	private ITipoChequeraRepository repository;

	@Autowired
	private LectivoTotalService lectivoTotalService;

	public List<TipoChequera> findAll() {
		return repository.findAll();
	}

	public List<TipoChequera> findAllAsignable(Integer facultadId, Integer lectivoId, Integer geograficaId,
											   Integer claseChequeraId) {
		List<Integer> tipoChequeraIds = lectivoTotalService.findAllByLectivo(facultadId, lectivoId).stream()
				.map(total -> total.getTipoChequeraId()).distinct().collect(Collectors.toList());
		return repository.findAllByTipoChequeraIdInAndGeograficaIdAndClaseChequeraId(tipoChequeraIds, geograficaId,
				claseChequeraId, Sort.by("nombre").ascending());
	}

	public List<TipoChequera> findAllByFacultadIdAndGeograficaId(Integer facultadId, Integer geograficaId) {
		List<Integer> tipoChequeraIds = lectivoTotalService.findAllByFacultadId(facultadId).stream()
				.map(total -> total.getTipoChequeraId()).distinct().collect(Collectors.toList());
		return repository.findAllByTipoChequeraIdInAndGeograficaId(tipoChequeraIds, geograficaId);
	}

	public List<TipoChequera> findAllByClaseChequera(Integer claseChequeraId) {
		return repository.findAllByClaseChequeraId(claseChequeraId);
	}

	public List<TipoChequera> findAllByClaseChequeraIds(List<Integer> claseChequeraIds) {
		return repository.findAllByClaseChequeraIdIn(claseChequeraIds);
	}

	public TipoChequera findByTipoChequeraId(Integer tipoChequeraId) {
		return repository.findByTipoChequeraId(tipoChequeraId)
				.orElseThrow(() -> new TipoChequeraException(tipoChequeraId));
	}

	public TipoChequera findLast() {
		return repository.findTopByOrderByTipoChequeraIdDesc().orElseThrow(() -> new TipoChequeraException());
	}

	public TipoChequera add(TipoChequera tipoChequera) {
		repository.save(tipoChequera);
		return tipoChequera;
	}

	public TipoChequera update(TipoChequera newTipoChequera, Integer tipoChequeraId) {
		return repository.findByTipoChequeraId(tipoChequeraId).map(tipoChequera -> {
			tipoChequera = new TipoChequera(tipoChequeraId, newTipoChequera.getNombre(), newTipoChequera.getPrefijo(), newTipoChequera.getGeograficaId(), newTipoChequera.getClaseChequeraId(), newTipoChequera.getImprimir(), newTipoChequera.getContado(), newTipoChequera.getMultiple(), null, null);
			return repository.save(tipoChequera);
		}).orElseThrow(() -> new TipoChequeraException(tipoChequeraId));
	}

	public void delete(Integer tipoChequeraId) {
		repository.deleteByTipoChequeraId(tipoChequeraId);
	}

	@Transactional
	public void unmark() {
		List<TipoChequera> tipos = repository.findAll();
		for (TipoChequera tipo : tipos) {
			tipo.setImprimir((byte) 0);
		}
		repository.saveAll(tipos);
	}

	public TipoChequera mark(Integer tipochequeraId, Byte imprimir) {
		return repository.findByTipoChequeraId(tipochequeraId).map(tipochequera -> {
			tipochequera.setImprimir(imprimir);
			return repository.save(tipochequera);
		}).orElseThrow(() -> new TipoChequeraException(tipochequeraId));
	}

}
