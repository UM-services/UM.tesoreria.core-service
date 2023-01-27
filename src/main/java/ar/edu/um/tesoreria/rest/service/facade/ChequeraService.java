/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.um.tesoreria.rest.exception.ChequeraEliminadaException;
import ar.edu.um.tesoreria.rest.exception.ChequeraSerieControlException;
import ar.edu.um.tesoreria.rest.exception.DebitoException;
import ar.edu.um.tesoreria.rest.model.ChequeraAlternativa;
import ar.edu.um.tesoreria.rest.model.ChequeraCuota;
import ar.edu.um.tesoreria.rest.model.ChequeraEliminada;
import ar.edu.um.tesoreria.rest.model.ChequeraImpresionCabecera;
import ar.edu.um.tesoreria.rest.model.ChequeraImpresionDetalle;
import ar.edu.um.tesoreria.rest.model.ChequeraSerie;
import ar.edu.um.tesoreria.rest.model.ChequeraSerieControl;
import ar.edu.um.tesoreria.rest.model.ChequeraTotal;
import ar.edu.um.tesoreria.rest.model.Debito;
import ar.edu.um.tesoreria.rest.service.ChequeraAlternativaService;
import ar.edu.um.tesoreria.rest.service.ChequeraCuotaService;
import ar.edu.um.tesoreria.rest.service.ChequeraEliminadaService;
import ar.edu.um.tesoreria.rest.service.ChequeraImpresionCabeceraService;
import ar.edu.um.tesoreria.rest.service.ChequeraImpresionDetalleService;
import ar.edu.um.tesoreria.rest.service.ChequeraPagoAsientoService;
import ar.edu.um.tesoreria.rest.service.ChequeraPagoService;
import ar.edu.um.tesoreria.rest.service.ChequeraSerieControlService;
import ar.edu.um.tesoreria.rest.service.ChequeraSerieService;
import ar.edu.um.tesoreria.rest.service.ChequeraTotalService;
import ar.edu.um.tesoreria.rest.service.DebitoService;
import ar.edu.um.tesoreria.rest.util.Tool;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class ChequeraService {

	@Autowired
	private ChequeraEliminadaService chequeraEliminadaService;

	@Autowired
	private ChequeraPagoAsientoService chequeraPagoAsientoService;

	@Autowired
	private ChequeraPagoService chequeraPagoService;

	@Autowired
	private ChequeraCuotaService chequeraCuotaService;

	@Autowired
	private ChequeraAlternativaService chequeraAlternativaService;

	@Autowired
	private ChequeraTotalService chequeraTotalService;

	@Autowired
	private ChequeraSerieService chequeraSerieService;

	@Autowired
	private ChequeraSerieControlService chequeraSerieControlService;

	@Autowired
	private ChequeraImpresionCabeceraService chequeraImpresionCabeceraService;

	@Autowired
	private ChequeraImpresionDetalleService chequeraImpresionDetalleService;

	@Autowired
	private DebitoService debitoService;

	@Transactional
	public void deleteByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, String usuarioId) {

		// Elimina el asiento asociado
		chequeraPagoAsientoService.deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(facultadId, tipoChequeraId,
				chequeraSerieId);

		// Elimina los pagos asociados
		chequeraPagoService.deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(facultadId, tipoChequeraId,
				chequeraSerieId);

		// Elimina las cuotas asociadas
		chequeraCuotaService.deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(facultadId, tipoChequeraId,
				chequeraSerieId);

		// Elimina las alternativas asociadas
		chequeraAlternativaService.deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(facultadId, tipoChequeraId,
				chequeraSerieId);

		// Elimina los totales asociados
		chequeraTotalService.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId,
				chequeraSerieId);

		// Elimina chequera
		ChequeraSerie chequeraserie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
		chequeraSerieService.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId,
				chequeraSerieId);

		// Marca chequera serie control como eliminada
		ChequeraSerieControl chequeraSerieControl = null;
		try {
			chequeraSerieControl = chequeraSerieControlService.findByUnique(facultadId, tipoChequeraId,
					chequeraSerieId);
			chequeraSerieControl.setEliminada((byte) 1);
			chequeraSerieControlService.update(chequeraSerieControl, chequeraSerieControl.getChequeraSerieControlId());
		} catch (ChequeraSerieControlException e) {
			chequeraSerieControl = new ChequeraSerieControl(null, facultadId, tipoChequeraId, chequeraSerieId, (byte) 0,
					null, null, null, (byte) 1);
			chequeraSerieControlService.add(chequeraSerieControl);
		}

		// Registra chequera eliminada
		try {
			chequeraEliminadaService.findByUnique(chequeraserie.getFacultadId(), chequeraserie.getTipoChequeraId(),
					chequeraserie.getChequeraSerieId());
		} catch (ChequeraEliminadaException e) {
			chequeraEliminadaService.add(new ChequeraEliminada(null, chequeraserie.getFacultadId(),
					chequeraserie.getTipoChequeraId(), chequeraserie.getChequeraId(), chequeraserie.getPersonaId(),
					chequeraserie.getDocumentoId(), chequeraserie.getLectivoId(), chequeraserie.getArancelTipoId(),
					chequeraserie.getCursoId(), chequeraserie.getAsentado(), chequeraserie.getGeograficaId(),
					chequeraserie.getFecha(), chequeraserie.getCuotasPagadas(), chequeraserie.getObservaciones(),
					chequeraserie.getAlternativaId(), chequeraserie.getAlgoPagado(), chequeraserie.getTipoImpresionId(),
					usuarioId));
		}
	}

	@Transactional
	public void extendDebito(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
		// Buscar la última cuota del debito
		ChequeraSerie serie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
		log.debug("Serie -> {}", serie);
		Debito lastDebito = null;
		try {
			lastDebito = debitoService.findLastByChequera(facultadId, tipoChequeraId, chequeraSerieId,
					serie.getAlternativaId());
			// Si tiene fecha de baja no se debe extender
			if (lastDebito.getFechaBaja() != null) {
				return;
			}
		} catch (DebitoException e) {
			return;
		}
		// Si tiene débito asociado y no está de baja extender hasta la última cuota
		Map<String, Debito> debitos = debitoService
				.findAllByAlternativa(facultadId, tipoChequeraId, chequeraSerieId, serie.getAlternativaId(),
						lastDebito.getDebitoTipoId())
				.stream().collect(Collectors.toMap(Debito::cuotaKey, debito -> debito));
		List<Debito> newDebitos = new ArrayList<Debito>();
		for (ChequeraCuota cuota : chequeraCuotaService
				.findAllByFacultadIdAndTipochequeraIdAndChequeraserieIdAndAlternativaId(facultadId, tipoChequeraId,
						chequeraSerieId, serie.getAlternativaId())) {
			if (!debitos.containsKey(cuota.cuotaKey())) {
				if (cuota.getImporte1().compareTo(BigDecimal.ZERO) > 0
						&& cuota.getVencimiento1().isAfter(Tool.dateAbsoluteArgentina())) {
					newDebitos.add(new Debito(null, facultadId, tipoChequeraId, chequeraSerieId, serie.getChequeraId(),
							cuota.getProductoId(), cuota.getAlternativaId(), cuota.getCuotaId(),
							cuota.getVencimiento1(), null, lastDebito.getDebitoTipoId(), lastDebito.getCbu(),
							lastDebito.getNumeroTarjeta(), lastDebito.getObservaciones(), null, (byte) 0, "", null,
							null, null));
				}
			}
		}
		log.debug("Debitos -> {}", newDebitos);
		newDebitos = debitoService.saveAll(newDebitos);
	}

	@Transactional
	public void track(Long chequeraId) {
		ChequeraSerie serie = chequeraSerieService.findByChequeraId(chequeraId);
		ChequeraImpresionCabecera cabecera = new ChequeraImpresionCabecera(null, serie.getFacultadId(),
				serie.getTipoChequeraId(), serie.getChequeraSerieId(), Tool.hourAbsoluteArgentina(),
				serie.getPersonaId(), serie.getDocumentoId(), serie.getLectivoId(), serie.getGeograficaId(),
				serie.getArancelTipoId(), serie.getAlternativaId(), serie.getUsuarioId());
		cabecera = chequeraImpresionCabeceraService.add(cabecera);
		Map<Integer, ChequeraTotal> totales = chequeraTotalService
				.findAllByChequera(serie.getFacultadId(), serie.getTipoChequeraId(), serie.getChequeraSerieId())
				.stream().collect(Collectors.toMap(ChequeraTotal::getProductoId, total -> total));
		Map<Integer, ChequeraAlternativa> alternativas = chequeraAlternativaService
				.findAllByChequera(serie.getFacultadId(), serie.getTipoChequeraId(), serie.getChequeraSerieId(),
						serie.getAlternativaId())
				.stream().collect(Collectors.toMap(ChequeraAlternativa::getProductoId, alternativa -> alternativa));
		List<ChequeraImpresionDetalle> detalles = new ArrayList<ChequeraImpresionDetalle>();
		for (ChequeraCuota cuota : chequeraCuotaService
				.findAllByFacultadIdAndTipochequeraIdAndChequeraserieIdAndAlternativaId(serie.getFacultadId(),
						serie.getTipoChequeraId(), serie.getChequeraSerieId(), serie.getAlternativaId())) {
			ChequeraTotal total = totales.get(cuota.getProductoId());
			ChequeraAlternativa alternativa = alternativas.get(cuota.getProductoId());
			detalles.add(new ChequeraImpresionDetalle(null, cabecera.getChequeraImpresionCabeceraId(),
					cuota.getFacultadId(), cuota.getTipoChequeraId(), cuota.getChequeraSerieId(), cuota.getProductoId(),
					cuota.getAlternativaId(), cuota.getCuotaId(), alternativa.getTitulo(), total.getTotal(),
					alternativa.getCuotas(), cuota.getMes(), cuota.getAnho(), cuota.getArancelTipoId(),
					cuota.getVencimiento1(), cuota.getImporte1(), cuota.getVencimiento2(), cuota.getImporte2(),
					cuota.getVencimiento3(), cuota.getImporte3(), serie.getUsuarioId()));
		}
		detalles = chequeraImpresionDetalleService.saveAll(detalles);
	}

}
