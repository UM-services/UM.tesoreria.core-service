/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.CarreraChequeraException;
import um.tesoreria.core.kotlin.model.CarreraChequera;
import um.tesoreria.core.repository.ICarreraChequeraRepository;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
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
		var carreraChequera = repository
				.findByFacultadIdAndLectivoIdAndPlanIdAndCarreraIdAndClaseChequeraIdAndCursoAndGeograficaId(facultadId, lectivoId, planId,
						carreraId, claseChequeraId, curso, geograficaId)
				.orElseThrow(() -> new CarreraChequeraException(facultadId, lectivoId, planId, carreraId, curso,
						geograficaId));
        try {
            log.debug("CarreraChequera found: {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(carreraChequera));
        } catch (JsonProcessingException e) {
            log.debug("CarreraChequera error: {}", e.getMessage());
        }
        return carreraChequera;
	}

	public CarreraChequera add(CarreraChequera carreraChequera) {
		return repository.save(carreraChequera);
	}

	public CarreraChequera update(CarreraChequera newCarreraChequera, Long carreraChequeraId) {
		return repository.findByCarreraChequeraId(carreraChequeraId).map(carreraChequera -> {
			carreraChequera = new CarreraChequera(carreraChequeraId, newCarreraChequera.getFacultadId(),
					newCarreraChequera.getLectivoId(), newCarreraChequera.getPlanId(),
					newCarreraChequera.getCarreraId(), newCarreraChequera.getClaseChequeraId(),
					newCarreraChequera.getCurso(), newCarreraChequera.getGeograficaId(),
					newCarreraChequera.getTipoChequeraId());
			return repository.save(carreraChequera);
		}).orElseThrow(() -> new CarreraChequeraException(carreraChequeraId));
	}

}
