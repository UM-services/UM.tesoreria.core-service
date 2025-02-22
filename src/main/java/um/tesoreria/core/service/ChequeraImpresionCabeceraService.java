/**
 * 
 */
package um.tesoreria.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ChequeraImpresionCabeceraException;
import um.tesoreria.core.kotlin.model.ChequeraImpresionCabecera;
import um.tesoreria.core.repository.ChequeraImpresionCabeceraRepository;

import java.time.OffsetDateTime;

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

	public ChequeraImpresionCabecera findLastByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
		return repository.findFirstByFacultadIdAndTipoChequeraIdAndChequeraSerieIdOrderByChequeraImpresionCabeceraIdDesc(facultadId, tipoChequeraId, chequeraSerieId)
				.orElseThrow(() -> new ChequeraImpresionCabeceraException(facultadId, tipoChequeraId, chequeraSerieId));
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
