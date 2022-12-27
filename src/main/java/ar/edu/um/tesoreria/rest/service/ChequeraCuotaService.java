/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ChequeraCuotaNotFoundException;
import ar.edu.um.tesoreria.rest.exception.ChequeraSerieNotFoundException;
import ar.edu.um.tesoreria.rest.exception.FacultadNotFoundException;
import ar.edu.um.tesoreria.rest.exception.LectivoNotFoundException;
import ar.edu.um.tesoreria.rest.exception.TipoChequeraNotFoundException;
import ar.edu.um.tesoreria.rest.model.ChequeraCuota;
import ar.edu.um.tesoreria.rest.model.ChequeraPago;
import ar.edu.um.tesoreria.rest.model.ChequeraSerie;
import ar.edu.um.tesoreria.rest.model.ChequeraTotal;
import ar.edu.um.tesoreria.rest.model.Facultad;
import ar.edu.um.tesoreria.rest.model.Lectivo;
import ar.edu.um.tesoreria.rest.model.TipoChequera;
import ar.edu.um.tesoreria.rest.model.dto.DeudaChequera;
import ar.edu.um.tesoreria.rest.repository.IChequeraCuotaRepository;
import ar.edu.um.tesoreria.rest.util.Tool;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraCuotaService {

	@Autowired
	private IChequeraCuotaRepository repository;

	@Autowired
	private ChequeraSerieService chequeraSerieService;

	@Autowired
	private ChequeraPagoService chequeraPagoService;

	@Autowired
	private ChequeraTotalService chequeraTotalService;

	@Autowired
	private FacultadService facultadService;

	@Autowired
	private TipoChequeraService tipoChequeraService;

	@Autowired
	private LectivoService lectivoService;

	public List<ChequeraCuota> findAllByFacultadIdAndTipochequeraIdAndChequeraserieIdAndAlternativaId(
			Integer facultadId, Integer tipochequeraId, Long chequeraserieId, Integer alternativaId) {
		return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(facultadId,
				tipochequeraId, chequeraserieId, alternativaId, Sort.by("vencimiento1").ascending()
						.and(Sort.by("productoId").ascending().and(Sort.by("cuotaId").ascending())));
	}

	public List<ChequeraCuota> findAllDebidas(Integer facultadId, Integer tipochequeraId, Long chequeraserieId,
			Integer alternativaId) {
		return repository
				.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndPagadoAndVencimiento1LessThanEqualAndImporte1GreaterThan(
						facultadId, tipochequeraId, chequeraserieId, alternativaId, (byte) 0, (byte) 0,
						OffsetDateTime.now(), BigDecimal.ZERO);
	}

	public ChequeraCuota findByChequeraCuotaId(Long chequeraCuotaId) {
		return repository.findByChequeraCuotaId(chequeraCuotaId)
				.orElseThrow(() -> new ChequeraCuotaNotFoundException(chequeraCuotaId));
	}

	public ChequeraCuota findByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
			Integer productoId, Integer alternativaId, Integer cuotaId) {
		return repository
				.findByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaId(facultadId,
						tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId)
				.orElseThrow(() -> new ChequeraCuotaNotFoundException(facultadId, tipoChequeraId, chequeraSerieId,
						productoId, alternativaId, cuotaId));
	}

	@Transactional
	public List<ChequeraCuota> saveAll(List<ChequeraCuota> chequeraCuotas) {
		chequeraCuotas = repository.saveAll(chequeraCuotas);
		return chequeraCuotas;
	}

	@Transactional
	public void deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(Integer facultadId, Integer tipochequeraId,
			Long chequeraserieId) {
		repository.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipochequeraId,
				chequeraserieId);
	}

	public DeudaChequera calculateDeuda(Integer facultadId, Integer tipochequeraId, Long chequeraserieId) {
		ChequeraSerie serie = null;
		try {
			serie = chequeraSerieService.findByUnique(facultadId, tipochequeraId, chequeraserieId);
		} catch (ChequeraSerieNotFoundException e) {
			return new DeudaChequera(BigDecimal.ZERO, 0, facultadId, "", tipochequeraId, "", chequeraserieId, 0, "", 1,
					BigDecimal.ZERO, new BigDecimal(1000000), 1000, null);
		}
		Map<String, ChequeraPago> pagos = chequeraPagoService
				.findAllByChequera(facultadId, tipochequeraId, chequeraserieId).stream()
				.collect(Collectors.toMap(ChequeraPago::getCuotaKey, Function.identity(), (pago, replacement) -> pago));
		BigDecimal deuda = BigDecimal.ZERO;
		Integer cantidad = 0;
		for (ChequeraCuota cuota : repository
				.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndPagadoAndVencimiento1LessThanEqualAndImporte1GreaterThan(
						facultadId, tipochequeraId, chequeraserieId, serie.getAlternativaId(), (byte) 0, (byte) 0,
						Tool.hourAbsoluteArgentina(), BigDecimal.ZERO)) {
			BigDecimal deudaCuota = cuota.getImporte1();
			if (pagos.containsKey(cuota.cuotaKey())) {
				deudaCuota = deudaCuota.subtract(pagos.get(cuota.cuotaKey()).getImporte()).setScale(2,
						RoundingMode.HALF_UP);
			}
			if (deudaCuota.compareTo(BigDecimal.ZERO) < 0) {
				deudaCuota = BigDecimal.ZERO;
			}
			if (deudaCuota.compareTo(BigDecimal.ZERO) > 0) {
				cantidad++;
			}
			deuda = deuda.add(deudaCuota).setScale(2, RoundingMode.HALF_UP);
		}
		BigDecimal total = chequeraTotalService.findAllByChequera(facultadId, tipochequeraId, chequeraserieId).stream()
				.map(ChequeraTotal::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
		return new DeudaChequera(serie.getPersonaId(), serie.getDocumentoId(), facultadId, "", tipochequeraId, "",
				chequeraserieId, serie.getLectivoId(), "", serie.getAlternativaId(), total, deuda, cantidad,
				serie.getChequeraId());
	}

	public DeudaChequera calculateDeudaExtended(Integer facultadId, Integer tipochequeraId, Long chequeraserieId) {
		ChequeraSerie serie = null;
		try {
			serie = chequeraSerieService.findByUnique(facultadId, tipochequeraId, chequeraserieId);
		} catch (ChequeraSerieNotFoundException e) {
			return new DeudaChequera(BigDecimal.ZERO, 0, facultadId, "", tipochequeraId, "", chequeraserieId, 0, "", 1,
					BigDecimal.ZERO, new BigDecimal(1000000), 1000, null);
		}
		Map<String, ChequeraPago> pagos = chequeraPagoService
				.findAllByChequera(facultadId, tipochequeraId, chequeraserieId).stream()
				.collect(Collectors.toMap(ChequeraPago::getCuotaKey, Function.identity(), (pago, replacement) -> pago));
		BigDecimal deuda = BigDecimal.ZERO;
		Integer cantidad = 0;
		for (ChequeraCuota cuota : repository
				.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndPagadoAndVencimiento1LessThanEqualAndImporte1GreaterThan(
						facultadId, tipochequeraId, chequeraserieId, serie.getAlternativaId(), (byte) 0, (byte) 0,
						Tool.hourAbsoluteArgentina(), BigDecimal.ZERO)) {
			BigDecimal deudaCuota = cuota.getImporte1();
			if (pagos.containsKey(cuota.cuotaKey())) {
				deudaCuota = deudaCuota.subtract(pagos.get(cuota.cuotaKey()).getImporte()).setScale(2,
						RoundingMode.HALF_UP);
			}
			if (deudaCuota.compareTo(BigDecimal.ZERO) < 0) {
				deudaCuota = BigDecimal.ZERO;
			}
			if (deudaCuota.compareTo(BigDecimal.ZERO) > 0) {
				cantidad++;
			}
			deuda = deuda.add(deudaCuota).setScale(2, RoundingMode.HALF_UP);
		}
		BigDecimal total = chequeraTotalService.findAllByChequera(facultadId, tipochequeraId, chequeraserieId).stream()
				.map(ChequeraTotal::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
		Facultad facultad = null;
		try {
			facultad = facultadService.findByFacultadId(facultadId);
		} catch (FacultadNotFoundException e) {
			facultad = new Facultad();
		}
		TipoChequera tipo = null;
		try {
			tipo = tipoChequeraService.findByTipoChequeraId(tipochequeraId);
		} catch (TipoChequeraNotFoundException e) {
			tipo = new TipoChequera();
		}
		Lectivo lectivo = null;
		try {
			lectivo = lectivoService.findByLectivoId(serie.getLectivoId());
		} catch (LectivoNotFoundException e) {
			lectivo = new Lectivo();
		}
		return new DeudaChequera(serie.getPersonaId(), serie.getDocumentoId(), facultadId, facultad.getNombre(),
				tipochequeraId, tipo.getNombre(), chequeraserieId, serie.getLectivoId(), lectivo.getNombre(),
				serie.getAlternativaId(), total, deuda, cantidad, serie.getChequeraId());
	}

	public String calculateCodigoBarras(ChequeraCuota chequeraCuota) {
		String codigoBarras = "";

		if (chequeraCuota.getVencimiento3().compareTo(chequeraCuota.getVencimiento2()) < 0) {
			chequeraCuota.setVencimiento2(chequeraCuota.getVencimiento3());
		}
		if (chequeraCuota.getVencimiento2().compareTo(chequeraCuota.getVencimiento1()) < 0) {
			chequeraCuota.setVencimiento1(chequeraCuota.getVencimiento2());
		}
		// Codigo Gire
		codigoBarras += "255";
		// Codigo Unidad Academica
		codigoBarras += new DecimalFormat("00").format(chequeraCuota.getFacultadId());
		// Tipo de Chequera
		codigoBarras += new DecimalFormat("000").format(chequeraCuota.getTipoChequeraId());
		// Numero de Chequera
		codigoBarras += new DecimalFormat("00000").format(chequeraCuota.getChequeraSerieId());
		// Producto
		codigoBarras += new DecimalFormat("0").format(chequeraCuota.getProductoId());
		// Mes
		codigoBarras += new DecimalFormat("00").format(chequeraCuota.getMes());
		// Año
		codigoBarras += new DecimalFormat("00").format(chequeraCuota.getAnho() - 2000);
		// Cuota ID
		codigoBarras += new DecimalFormat("00").format(chequeraCuota.getCuotaId());
		// 1er Importe
		codigoBarras += new DecimalFormat("0000000").format(chequeraCuota.getImporte1());
		OffsetDateTime vencimiento1 = chequeraCuota.getVencimiento1().plusHours(3);
		OffsetDateTime vencimiento2 = chequeraCuota.getVencimiento2().plusHours(3);
		OffsetDateTime vencimiento3 = chequeraCuota.getVencimiento3().plusHours(3);
		// Dia 1er Vencimiento
		codigoBarras += new DecimalFormat("00").format(vencimiento1.getDayOfMonth());
		// Mes 1er Vencimiento
		codigoBarras += new DecimalFormat("00").format(vencimiento1.getMonthValue());
		// Año 1er Vencimiento
		codigoBarras += new DecimalFormat("0000").format(vencimiento1.getYear());
		// Dif 2do Importe
		codigoBarras += new DecimalFormat("00000")
				.format(chequeraCuota.getImporte2().subtract(chequeraCuota.getImporte1()).setScale(0));
		// Dif 2do Vencimiento
		Long diferencia1 = ChronoUnit.DAYS.between(vencimiento1.toLocalDate(), vencimiento2.toLocalDate());
		codigoBarras += new DecimalFormat("000").format(diferencia1);
		// Dif 3er Importe
		codigoBarras += new DecimalFormat("00000")
				.format(chequeraCuota.getImporte3().subtract(chequeraCuota.getImporte2()).setScale(0));
		// Dif 3er Vencimiento
		Long diferencia2 = ChronoUnit.DAYS.between(vencimiento2.toLocalDate(), vencimiento3.toLocalDate());
		codigoBarras += new DecimalFormat("000").format(diferencia2);
		// Codigo Verificador
		codigoBarras += Tool.calculateVerificadorRapiPago(codigoBarras);

		return codigoBarras;
	}

}
