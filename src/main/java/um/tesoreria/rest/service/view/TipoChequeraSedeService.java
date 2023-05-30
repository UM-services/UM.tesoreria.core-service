/**
 * 
 */
package um.tesoreria.rest.service.view;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.exception.view.TipoChequeraSedeException;
import um.tesoreria.rest.model.view.TipoChequeraSede;
import um.tesoreria.rest.repository.view.ITipoChequeraSedeRepository;
import um.tesoreria.rest.service.ChequeraSerieService;
import um.tesoreria.rest.exception.view.TipoChequeraSedeException;
import um.tesoreria.rest.repository.view.ITipoChequeraSedeRepository;
import um.tesoreria.rest.service.ChequeraSerieService;

/**
 * @author daniel
 *
 */
@Service
public class TipoChequeraSedeService {

	@Autowired
	private ITipoChequeraSedeRepository repository;

	@Autowired
	private ChequeraSerieService chequeraSerieService;

	public List<TipoChequeraSede> findAll() {
		return repository.findAll();
	}

	public List<TipoChequeraSede> findAllByGeograficaId(Integer geograficaId, String modulo) {
		if (geograficaId > 0 && !modulo.equals("STesore"))
			return repository.findAllByGeograficaId(geograficaId);
		return repository.findAll();
	}

	public List<TipoChequeraSede> findAllByLectivoIdAndFacultadId(Integer lectivoId, Integer facultadId,
			Integer geograficaId, String modulo) {
		if (geograficaId > 0 && !modulo.equals("STesore"))
			return repository.findAllById(chequeraSerieService
					.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId, facultadId, geograficaId).stream()
					.map(chequera -> chequera.getTipoChequeraId()).distinct().collect(Collectors.toList()));
		return repository.findAllById(chequeraSerieService.findAllByLectivoIdAndFacultadId(lectivoId, facultadId)
				.stream().map(chequera -> chequera.getTipoChequeraId()).distinct().collect(Collectors.toList()));
	}

	public List<TipoChequeraSede> findAllByPersonaId(BigDecimal personaId, Integer documentoId, Integer lectivoId,
			Integer facultadId) {
		return repository.findAllById(chequeraSerieService
				.findAllByPersonaIdAndDocumentoIdAndLectivoIdAndFacultadId(personaId, documentoId, lectivoId,
						facultadId)
				.stream().map(chequera -> chequera.getTipoChequeraId()).distinct().collect(Collectors.toList()));
	}

	public List<TipoChequeraSede> findAllByLectivoIdAndGeograficaId(Integer lectivoId, Integer geograficaId) {
		List<Integer> facultadIds = List.of(1, 2, 3, 4, 5, 14, 15);
		List<Integer> lectivoIds = List.of(lectivoId - 1, lectivoId);
		List<Integer> tipoChequeraIds = chequeraSerieService
				.findAllByGeograficaIdAndFacultadIdInAndLectivoIdIn(geograficaId, facultadIds, lectivoIds).stream()
				.map(chequera -> chequera.getTipoChequeraId()).distinct().collect(Collectors.toList());
		List<Integer> claseChequeraIds = List.of(2, 5);
		return repository.findAllByTipoChequeraIdInAndClaseChequeraIdIn(tipoChequeraIds, claseChequeraIds);
	}

	public TipoChequeraSede findByTipoChequeraId(Integer tipoChequeraId) {
		return repository.findByTipoChequeraId(tipoChequeraId)
				.orElseThrow(() -> new TipoChequeraSedeException(tipoChequeraId));
	}
}
