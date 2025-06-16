/**
 * 
 */
package um.tesoreria.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.BajaException;
import um.tesoreria.core.kotlin.model.Baja;
import um.tesoreria.core.repository.BajaRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class BajaService {

	private final BajaRepository repository;

	public BajaService(BajaRepository repository) {
		this.repository = repository;
	}

	public List<Baja> findAllByChequeraIdIn(List<Long> chequeraIds) {
		return repository.findAllByChequeraIdIn(chequeraIds);
	}

	public Baja findByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
		log.debug("Processing BajaService.findByUnique");
		return repository
				.findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
				.map(baja -> {
					logBaja(baja);
                    return baja;
				})
				.orElseThrow(() -> new BajaException(facultadId, tipoChequeraId, chequeraSerieId));
	}

	public Baja add(Baja baja) {
		log.debug("Processing add");
		baja = repository.save(baja);
		logBaja(baja);
		return baja;
	}

	public Baja update(Baja newBaja, Long bajaId) {
		log.debug("Processing update");
		return repository.findByBajaId(bajaId).map(baja -> {
			baja = new Baja(newBaja.getBajaId(), newBaja.getFacultadId(), newBaja.getTipoChequeraId(),
					newBaja.getChequeraSerieId(), newBaja.getChequeraId(), newBaja.getLectivoId(),
					newBaja.getPersonaId(), newBaja.getDocumentoId(), newBaja.getFecha(), newBaja.getObservaciones(),
					newBaja.getEgresado(), null, null, null, null, null);
			baja = repository.save(baja);
			logBaja(baja);
			return baja;
		}).orElseThrow(() -> new BajaException(bajaId));
	}

	@Transactional
	public void deleteByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
		log.debug("Processing deleteByUnique");
		repository.deleteByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId);
	}

	private void logBaja(Baja baja) {
		try {
			log.debug("Baja -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(baja));
		} catch (JsonProcessingException e) {
			log.debug("Baja log error -> {}", e.getMessage());
		}
	}

}
