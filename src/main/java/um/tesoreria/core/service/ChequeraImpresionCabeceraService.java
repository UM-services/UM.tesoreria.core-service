/**
 * 
 */
package um.tesoreria.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.ChequeraImpresionCabecera;
import um.tesoreria.core.repository.ChequeraImpresionCabeceraRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class ChequeraImpresionCabeceraService {

	private final ChequeraImpresionCabeceraRepository repository;

	public ChequeraImpresionCabeceraService(ChequeraImpresionCabeceraRepository repository) {
		this.repository = repository;
	}

	public ChequeraImpresionCabecera add(ChequeraImpresionCabecera cabecera) {
		cabecera = repository.save(cabecera);
		logChequeraImpresionCabecera(cabecera);
		return cabecera;
	}

	public Optional<ChequeraImpresionCabecera> findLastByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
		return repository.findFirstByFacultadIdAndTipoChequeraIdAndChequeraSerieIdOrderByChequeraImpresionCabeceraIdDesc(facultadId, tipoChequeraId, chequeraSerieId);
	}

	public List<ChequeraImpresionCabecera> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
		return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdOrderByChequeraImpresionCabeceraIdDesc(
				facultadId, tipoChequeraId, chequeraSerieId);
	}

	/**
	 * Un reprint con el mismo contenido actualiza sólo la fecha del último snapshot en vez de
	 * insertar una cabecera + N detalles nuevos.
	 */
	public ChequeraImpresionCabecera touch(ChequeraImpresionCabecera cabecera, java.time.OffsetDateTime fecha) {
		cabecera.setFecha(fecha);
		return repository.save(cabecera);
	}

	public void deleteAllByIds(List<Long> chequeraImpresionCabeceraIds) {
		if (chequeraImpresionCabeceraIds == null || chequeraImpresionCabeceraIds.isEmpty()) {
			return;
		}
		repository.deleteAllByChequeraImpresionCabeceraIdIn(chequeraImpresionCabeceraIds);
	}

	private void logChequeraImpresionCabecera(ChequeraImpresionCabecera cabecera) {
		try {
			log.debug("ChequeraImpresionCabecera -> {}", JsonMapper
					.builder()
					.findAndAddModules()
					.build()
					.writerWithDefaultPrettyPrinter()
					.writeValueAsString(cabecera));
		} catch (JsonProcessingException e) {
			log.debug("ChequeraImpresionCabecera jsonify error -> {}", e.getMessage());
		}
	}

}
