/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.TipoChequeraException;
import ar.edu.um.tesoreria.rest.model.TipoChequera;
import ar.edu.um.tesoreria.rest.repository.ITipoChequeraRepository;

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

	public TipoChequera update(TipoChequera newtipochequera, Integer tipochequeraId) {
		return repository.findById(tipochequeraId).map(tipochequera -> {
			tipochequera.setNombre(newtipochequera.getNombre());
			tipochequera.setPrefijo(newtipochequera.getPrefijo());
			tipochequera.setGeograficaId(newtipochequera.getGeograficaId());
			tipochequera.setClaseChequeraId(newtipochequera.getClaseChequeraId());
			tipochequera.setImprimir(newtipochequera.getImprimir());
			tipochequera.setContado(newtipochequera.getContado());
			tipochequera.setMultiple(newtipochequera.getMultiple());
			return repository.save(tipochequera);
		}).orElseThrow(() -> new TipoChequeraException(tipochequeraId));
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
		return repository.findById(tipochequeraId).map(tipochequera -> {
			tipochequera.setImprimir(imprimir);
			return repository.save(tipochequera);
		}).orElseThrow(() -> new TipoChequeraException(tipochequeraId));
	}

}
