/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.TipoChequeraException;
import um.tesoreria.core.model.LectivoTotal;
import um.tesoreria.core.model.view.TipoChequeraSearch;
import um.tesoreria.core.repository.TipoChequeraRepository;
import um.tesoreria.core.kotlin.model.TipoChequera;
import um.tesoreria.core.service.view.TipoChequeraSearchService;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TipoChequeraService {

	private final TipoChequeraRepository repository;
	private final LectivoTotalService lectivoTotalService;
	private final TipoChequeraSearchService tipoChequeraSearchService;

	public List<TipoChequeraSearch> findAllByStrings(List<String> conditions) {
		log.debug("Processing findAllByStrings");
		return tipoChequeraSearchService.findAllByStrings(conditions);
	}

	public List<TipoChequera> findAll() {
		return repository.findAll();
	}

	public List<TipoChequera> findAllAsignable(Integer facultadId, Integer lectivoId, Integer geograficaId,
											   Integer claseChequeraId) {
		List<Integer> tipoChequeraIds = lectivoTotalService.findAllByLectivo(facultadId, lectivoId).stream()
				.map(LectivoTotal::getTipoChequeraId).distinct().collect(Collectors.toList());
		return repository.findAllByTipoChequeraIdInAndGeograficaIdAndClaseChequeraId(tipoChequeraIds, geograficaId,
				claseChequeraId, Sort.by("nombre").ascending());
	}

	public List<TipoChequera> findAllByFacultadIdAndGeograficaId(Integer facultadId, Integer geograficaId) {
		List<Integer> tipoChequeraIds = lectivoTotalService.findAllByFacultadId(facultadId).stream()
				.map(LectivoTotal::getTipoChequeraId).distinct().collect(Collectors.toList());
		return repository.findAllByTipoChequeraIdInAndGeograficaId(tipoChequeraIds, geograficaId);
	}

	public List<TipoChequera> findAllByClaseChequera(Integer claseChequeraId) {
		return repository.findAllByClaseChequeraId(claseChequeraId);
	}

	public List<TipoChequera> findAllByClaseChequeraIds(List<Integer> claseChequeraIds) {
		return repository.findAllByClaseChequeraIdIn(claseChequeraIds);
	}

	public TipoChequera findByTipoChequeraId(Integer tipoChequeraId) {
        var tipoChequera = repository.findByTipoChequeraId(tipoChequeraId)
                .orElseThrow(() -> new TipoChequeraException(tipoChequeraId));
        log.debug("TipoChequera -> {}", tipoChequera.jsonify());
		return tipoChequera;
	}

	public TipoChequera findLast() {
		return repository.findTopByOrderByTipoChequeraIdDesc().orElseThrow(TipoChequeraException::new);
	}

	public TipoChequera add(TipoChequera tipoChequera) {
		repository.save(tipoChequera);
		return tipoChequera;
	}

	public TipoChequera update(TipoChequera newTipoChequera, Integer tipoChequeraId) {
		return repository.findByTipoChequeraId(tipoChequeraId).map(tipoChequera -> {
			tipoChequera = new TipoChequera(tipoChequeraId,
					newTipoChequera.getNombre(),
					newTipoChequera.getPrefijo(),
					newTipoChequera.getGeograficaId(),
					newTipoChequera.getClaseChequeraId(),
					newTipoChequera.getImprimir(),
					newTipoChequera.getContado(),
					newTipoChequera.getMultiple(),
					newTipoChequera.getEmailCopia(),
					null,
					null);
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
