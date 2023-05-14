/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.CarreraChequera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.CarreraChequeraException;
import ar.edu.um.tesoreria.rest.repository.ICarreraChequeraRepository;

/**
 * @author daniel
 *
 */
@Service
public class CarreraChequeraService {

	@Autowired
	private ICarreraChequeraRepository repository;

	public List<CarreraChequera> findAllByFacultadIdAndLectivoIdAndGeograficaIdAndClaseChequeraIdAndCurso(Integer facultadId,
																										  Integer lectivoId, Integer geograficaId, Integer claseChequeraId, Integer curso) {
		return repository.findAllByFacultadIdAndLectivoIdAndGeograficaIdAndClaseChequeraIdAndCurso(facultadId,
				lectivoId, geograficaId, claseChequeraId, curso);
	}

	public CarreraChequera findByUnique(Integer facultadId, Integer lectivoId, Integer planId, Integer carreraId,
			Integer claseChequeraId, Integer curso, Integer geograficaId) {
		return repository
				.findByFacultadIdAndLectivoIdAndPlanIdAndCarreraIdAndClaseChequeraIdAndCursoAndGeograficaId(facultadId, lectivoId, planId,
						carreraId, claseChequeraId, curso, geograficaId)
				.orElseThrow(() -> new CarreraChequeraException(facultadId, lectivoId, planId, carreraId, curso,
						geograficaId));
	}

	public CarreraChequera add(CarreraChequera carreraChequera) {
		repository.save(carreraChequera);
		return carreraChequera;
	}

	public CarreraChequera update(CarreraChequera newCarreraChequera, Long carreraChequeraId) {
		return repository.findByCarreraChequeraId(carreraChequeraId).map(carreraChequera -> {
			carreraChequera = new CarreraChequera(carreraChequeraId, newCarreraChequera.getFacultadId(),
					newCarreraChequera.getLectivoId(), newCarreraChequera.getPlanId(),
					newCarreraChequera.getCarreraId(), newCarreraChequera.getClaseChequeraId(),
					newCarreraChequera.getCurso(), newCarreraChequera.getGeograficaId(),
					newCarreraChequera.getTipoChequeraId());
			repository.save(carreraChequera);
			return carreraChequera;
		}).orElseThrow(() -> new CarreraChequeraException(carreraChequeraId));
	}

}
