/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.view;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import ar.edu.um.tesoreria.rest.kotlin.model.view.ChequeraCuotaDeuda;
import ar.edu.um.tesoreria.rest.service.ClaseChequeraService;
import ar.edu.um.tesoreria.rest.service.TipoChequeraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.repository.view.IChequeraCuotaDeudaRepository;
import ar.edu.um.tesoreria.rest.util.Tool;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraCuotaDeudaService {

	@Autowired
	private IChequeraCuotaDeudaRepository repository;

	@Autowired
	private ClaseChequeraService claseChequeraService;

	@Autowired
	private TipoChequeraService tipoChequeraService;

	public List<ChequeraCuotaDeuda> findAllByRango(OffsetDateTime desde, OffsetDateTime hasta, Pageable pageable) {
		return repository.findAllByVencimiento1Between(Tool.firstTime(desde), Tool.lastTime(hasta), pageable);
	}

    public List<ChequeraCuotaDeuda> findAllPosgradoByRango(OffsetDateTime desde, OffsetDateTime hasta) {
		List<Integer> claseChequeraIds = claseChequeraService.findAllByPosgrado().stream().map(claseChequera -> claseChequera.getClaseChequeraId()).collect(Collectors.toList());
		List<Integer> tipoChequeraIds = tipoChequeraService.findAllByClaseChequeraIds(claseChequeraIds).stream().map(tipoChequera -> tipoChequera.getTipoChequeraId()).collect(Collectors.toList());
		return repository.findAllByTipoChequeraIdInAndVencimiento1Between(tipoChequeraIds, Tool.firstTime(desde), Tool.lastTime(hasta));
    }

}
