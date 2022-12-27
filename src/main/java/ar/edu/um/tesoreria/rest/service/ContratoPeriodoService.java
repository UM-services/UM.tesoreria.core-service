/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ContratoPeriodoNotFoundException;
import ar.edu.um.tesoreria.rest.model.ContratoPeriodo;
import ar.edu.um.tesoreria.rest.repository.IContratoPeriodoRepository;

/**
 * @author daniel
 *
 */
@Service
public class ContratoPeriodoService {

	@Autowired
	private IContratoPeriodoRepository repository;

	public List<ContratoPeriodo> findAllByContrato(Long contratoId) {
		return repository.findAllByContratoId(contratoId,
				Sort.by("anho").descending().and(Sort.by("mes").descending()));
	}

	public List<ContratoPeriodo> findAllByContratoFactura(Long contratoFacturaId) {
		return repository.findAllByContratoFacturaId(contratoFacturaId);
	}

	public List<ContratoPeriodo> findAllPendienteByContrato(Long contratoId) {
		return repository.findAllByContratoIdAndContratoFacturaIdIsNullAndContratoChequeIdIsNull(contratoId);
	}

	public List<ContratoPeriodo> findAllMarcadoByContrato(Long contratoId) {
		return repository.findAllByContratoIdAndMarcaTemporal(contratoId, (byte) 1);
	}

	public List<ContratoPeriodo> findAllByPeriodo(Integer anho, Integer mes) {
		return repository.findAllByAnhoAndMes(anho, mes);
	}

	public ContratoPeriodo findByContratoPeriodoId(Long contratoPeriodoId) {
		return repository.findByContratoPeriodoId(contratoPeriodoId)
				.orElseThrow(() -> new ContratoPeriodoNotFoundException(contratoPeriodoId));
	}

	public ContratoPeriodo findByPeriodo(Long contratoId, Integer anho, Integer mes) {
		return repository.findByContratoIdAndAnhoAndMes(contratoId, anho, mes)
				.orElseThrow(() -> new ContratoPeriodoNotFoundException(contratoId, anho, mes));
	}

	public ContratoPeriodo add(ContratoPeriodo contratoPeriodo) {
		contratoPeriodo = repository.save(contratoPeriodo);
		return contratoPeriodo;
	}

	public ContratoPeriodo update(ContratoPeriodo newcontratoperiodo, Long contratoperiodoId) {
		return repository.findByContratoPeriodoId(contratoperiodoId).map(contratoperiodo -> {
			contratoperiodo = new ContratoPeriodo(contratoperiodoId, newcontratoperiodo.getContratoId(),
					newcontratoperiodo.getAnho(), newcontratoperiodo.getMes(),
					newcontratoperiodo.getContratoFacturaId(), newcontratoperiodo.getContratoChequeId(),
					newcontratoperiodo.getImporte(), newcontratoperiodo.getMarcaTemporal(), null);
			contratoperiodo = repository.save(contratoperiodo);
			return contratoperiodo;
		}).orElseThrow(() -> new ContratoPeriodoNotFoundException(contratoperiodoId));
	}

	public List<ContratoPeriodo> saveAll(List<ContratoPeriodo> contratoperiodos) {
		repository.saveAll(contratoperiodos);
		return contratoperiodos;

	}

	@Transactional
	public void deleteByContratoPeriodoId(Long contratoPeriodoId) {
		repository.deleteByContratoPeriodoId(contratoPeriodoId);
	}

}
