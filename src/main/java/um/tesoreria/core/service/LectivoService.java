/**
 * 
 */
package um.tesoreria.core.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.LectivoException;
import um.tesoreria.core.kotlin.model.Lectivo;
import um.tesoreria.core.repository.ILectivoRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class LectivoService {

	@Autowired
	private ILectivoRepository repository;

	@Autowired
	private ChequeraSerieService chequeraserieservice;

	public List<Lectivo> findAll() {
		return repository.findAll(Sort.by("lectivoId").ascending());
	}

	public List<Lectivo> findAllReverse() {
		return repository.findAll(Sort.by("lectivoId").descending());
	}

	public List<Lectivo> findAllByPersona(BigDecimal personaId, Integer documentoId) {
		return repository.findAllByLectivoIdIn(
				chequeraserieservice.findAllByPersonaIdAndDocumentoId(personaId, documentoId, null).stream()
						.map(chequera -> chequera.getLectivoId()).collect(Collectors.toList()),
				Sort.by("lectivoId").descending());
	}

	public Lectivo findByFecha(OffsetDateTime fecha) {
		return repository.findByFechaInicioLessThanEqualAndFechaFinalGreaterThanEqual(fecha, fecha)
				.orElseThrow(() -> new LectivoException(fecha));
	}

	public Lectivo findByLectivoId(Integer lectivoId) {
		return repository.findByLectivoId(lectivoId).orElseThrow(() -> new LectivoException(lectivoId));
	}

	public Lectivo findLast() {
		return repository.findTopByOrderByLectivoIdDesc().orElseThrow(() -> new LectivoException());
	}

	public Lectivo add(Lectivo lectivo) {
		repository.save(lectivo);
		log.debug("Lectivo -> {}", lectivo);
		return lectivo;
	}

	@Transactional
	public void deleteByLectivoId(Integer lectivoId) {
		repository.deleteById(lectivoId);
	}

}
