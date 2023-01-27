/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.BajaException;
import ar.edu.um.tesoreria.rest.model.Baja;
import ar.edu.um.tesoreria.rest.repository.IBajaRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class BajaService {

	@Autowired
	private IBajaRepository repository;

	public List<Baja> findAllByChequeraIdIn(List<Long> chequeraIds) {
		return repository.findAllByChequeraIdIn(chequeraIds);
	}

	public Baja findByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
		return repository
				.findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
				.orElseThrow(() -> new BajaException(facultadId, tipoChequeraId, chequeraSerieId));
	}

	public Baja add(Baja baja) {
		baja = repository.save(baja);
		log.debug("Baja -> {}", baja);
		return baja;
	}

	public Baja update(Baja newBaja, Long bajaId) {
		return repository.findByBajaId(bajaId).map(baja -> {
			baja = new Baja(newBaja.getBajaId(), newBaja.getFacultadId(), newBaja.getTipoChequeraId(),
					newBaja.getChequeraSerieId(), newBaja.getChequeraId(), newBaja.getLectivoId(),
					newBaja.getPersonaId(), newBaja.getDocumentoId(), newBaja.getFecha(), newBaja.getObservaciones(),
					newBaja.getEgresado(), null, null, null, null, null);
			baja = repository.save(baja);
			log.debug("Baja -> {}", baja);
			return baja;
		}).orElseThrow(() -> new BajaException(bajaId));
	}

	@Transactional
	public void deleteByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
		repository.deleteByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId);
	}

}
