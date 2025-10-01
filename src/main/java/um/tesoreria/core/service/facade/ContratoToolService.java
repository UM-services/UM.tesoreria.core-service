/**
 * 
 */
package um.tesoreria.core.service.facade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.CursoCargoContratado;
import um.tesoreria.core.model.Contrato;
import um.tesoreria.core.model.ContratoFactura;
import um.tesoreria.core.model.ContratoPeriodo;
import um.tesoreria.core.service.ContratoFacturaService;
import um.tesoreria.core.service.ContratoPeriodoService;
import um.tesoreria.core.service.ContratoService;
import um.tesoreria.core.service.CursoCargoContratadoService;
import um.tesoreria.core.util.Jsonifier;
import um.tesoreria.core.util.Periodo;
import um.tesoreria.core.util.Tool;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ContratoToolService {

	private final ContratoFacturaService contratoFacturaService;
	private final ContratoPeriodoService contratoPeriodoService;
	private final ContratoService contratoService;
	private final CursoCargoContratadoService cursoCargoContratadoService;

	@Transactional
	public Boolean addFactura(ContratoFactura contratofactura) {
		contratoFacturaService.add(contratofactura);
		List<ContratoPeriodo> periodos = contratoPeriodoService
				.findAllMarcadoByContrato(contratofactura.getContratoId());
		for (ContratoPeriodo periodo : periodos) {
			periodo.setContratoFacturaId(contratofactura.getContratofacturaId());
			periodo.setMarcaTemporal((byte) 0);
		}
		contratoPeriodoService.saveAll(periodos);
		return true;
	}

	@Transactional
	public Boolean deleteFactura(Long contratofacturaId) {
		ContratoFactura factura = contratoFacturaService.findByContratofacturaId(contratofacturaId);
		if (factura.getPendiente() == 0) {
			return false;
		}
		for (ContratoPeriodo periodo : contratoPeriodoService.findAllByContratoFactura(contratofacturaId)) {
			periodo.setContratoFacturaId(null);
			contratoPeriodoService.update(periodo, periodo.getContratoPeriodoId());
		}
		contratoFacturaService.deleteByContratofacturaId(contratofacturaId);

		return true;
	}

	@Transactional
	public Boolean deleteContrato(Long contratoId) {
        log.debug("Processing ContratoToolService.deleteContrato(contratoId); {}", contratoId);
		for (ContratoPeriodo periodo : contratoPeriodoService.findAllPendienteByContrato(contratoId)) {
			log.debug("ContratoPeriodo -> {}", periodo.jsonify());
			contratoPeriodoService.deleteByContratoPeriodoId(periodo.getContratoPeriodoId());
		}
		contratoService.deleteByContratoId(contratoId);
		return true;
	}

	@Transactional
	public Boolean depuraContrato(Long contratoId) {
        log.debug("Processing ContratoToolService.depuraContrato");
		Contrato contrato = contratoService.findByContratoId(contratoId);
		long desde = contrato.getDesde().getYear() * 100L + contrato.getDesde().getMonthValue();
		long hasta = contrato.getHasta().getYear() * 100L + contrato.getHasta().getMonthValue();
		for (ContratoPeriodo periodo : contratoPeriodoService.findAllPendienteByContrato(contratoId)) {
            log.debug("ContratoPeriodo -> {}", periodo.jsonify());
			long periodo_ciclo = periodo.getAnho() * 100L + periodo.getMes();
			if (periodo_ciclo < desde) {
                log.debug("Eliminando ContratoPeriodo -> {}", periodo.getContratoPeriodoId());
				contratoPeriodoService.deleteByContratoPeriodoId(periodo.getContratoPeriodoId());
                // Agregar eliminación de cursos para este período
                log.debug("Eliminando CursoCargoContratado -> {}", periodo.getContratoId());
                cursoCargoContratadoService.deleteByContratoAndPeriodo(contratoId, periodo.getAnho(), periodo.getMes());
			}
			if (periodo_ciclo > hasta) {
                log.debug("Eliminando ContratoPeriodo -> {}", periodo.getContratoPeriodoId());
				contratoPeriodoService.deleteByContratoPeriodoId(periodo.getContratoPeriodoId());
                // Agregar eliminación de cursos para este período
                log.debug("Eliminando CursoCargoContratado -> {}", periodo.getContratoId());
                cursoCargoContratadoService.deleteByContratoAndPeriodo(contratoId, periodo.getAnho(), periodo.getMes());
			}
		}
		return true;
	}

	@Transactional
	public Boolean saveContrato(Contrato contrato) {
		if (contrato.getCanonMensualSinAjuste().compareTo(BigDecimal.ZERO) == 0) {
			contrato.setCanonMensualSinAjuste(contrato.getCanonMensual());
		}
		contrato.setCanonMensualLetras(Tool.number2Text(contrato.getCanonMensual()));
		contrato.setCanonTotal(contrato.getCanonMensual()
				.multiply(new BigDecimal(contrato.getMeses()).setScale(2, RoundingMode.HALF_UP)));
		contrato.setCanonTotalLetras(Tool.number2Text(contrato.getCanonTotal()));
		contrato.setPrimerVencimiento(
				Tool.lastDayOfMonth(contrato.getDesde().getYear(), contrato.getDesde().getMonthValue()));
		contrato.setMesesLetras(Tool.number2TextEntero(new BigDecimal(contrato.getMeses())));
		// Agrega o actualiza el contrato
		if (contrato.getContratoId() == null) {
			contrato = contratoService.add(contrato);
		} else {
			contrato = contratoService.update(contrato, contrato.getContratoId());
		}
		// Carga los períodos ya registrados
		Map<String, ContratoPeriodo> periodoKeys = contratoPeriodoService.findAllByContrato(contrato.getContratoId())
				.stream().collect(Collectors.toMap(ContratoPeriodo::periodoKey, periodo -> periodo));
		// Busca los períodos incluídos
		Long desde = contrato.getDesde().getYear() * 100L + contrato.getDesde().getMonthValue();
		Long hasta = contrato.getHasta().getYear() * 100L + contrato.getHasta().getMonthValue();
		List<ContratoPeriodo> periodos = new ArrayList<ContratoPeriodo>();
		for (Periodo mes : Periodo.makePeriodos(desde, hasta)) {
			if (periodoKeys.containsKey(mes.periodoKey())) {
				ContratoPeriodo periodo = periodoKeys.get(mes.periodoKey());
				if (periodo.getContratoFacturaId() == null && periodo.getContratoChequeId() == null) {
					periodo.setImporte(contrato.getCanonMensual());
					periodos.add(periodo);
				}
			} else {
				periodos.add(new ContratoPeriodo(null, contrato.getContratoId(), mes.getAnho(), mes.getMes(), null,
						null, contrato.getCanonMensual(), (byte) 0, null));
			}
		}
		periodos = contratoPeriodoService.saveAll(periodos);
        log.debug("Periodos -> {}", Jsonifier.builder(periodos).build());
		// Depura contrato
		this.depuraContrato(contrato.getContratoId());
		return true;
	}

	@Transactional
	public Boolean anulaEnvio(OffsetDateTime fecha, Integer envio) {
		List<ContratoFactura> facturas = contratoFacturaService.findAllByEnvio(fecha, envio);
		for (ContratoFactura factura : facturas) {
			factura.setPendiente((byte) 1);
			factura.setAcreditacion(null);
			factura.setEnvio(0);
		}
		facturas = contratoFacturaService.saveAll(facturas);
		return true;
	}

	@Transactional
	public Boolean saveCurso(Long cursoId, Long contratoId, Integer cargoTipoId, BigDecimal horasSemanales) {
		log.debug("Processing ContratoToolService.saveCurso");
		log.debug("ContratoToolService.saveCurso - Leyendo contrato -> {}",  contratoId);
        var contrato = contratoService.findByContratoId(contratoId);
        log.debug("Contrato -> {}",  contrato.jsonify());
		OffsetDateTime desde = contrato.getDesde().withOffsetSameInstant(ZoneOffset.UTC);
		OffsetDateTime hasta = contrato.getHasta().withOffsetSameInstant(ZoneOffset.UTC);
		// Armar periodos
		log.debug("ContratoToolService.saveCurso - Armando periodos");
		List<Periodo> periodos = Periodo.makePeriodos(desde.getYear() * 100L + desde.getMonthValue(),
				hasta.getYear() * 100L + hasta.getMonthValue());
        periodos.forEach(periodo -> log.debug("Periodo -> {}", periodo.jsonify()));
		// Recupera registrados
		log.debug("ContratoToolService.saveCurso - Leyendo cursos asignados");
		var cursoCargos = cursoCargoContratadoService.findAllByCursoIdAndContratoId(cursoId, contratoId);
        cursoCargos.forEach(cursoCargoContratado -> log.debug("CursoCargoContratado -> {}", cursoCargoContratado.jsonify()));
		Map<String, CursoCargoContratado> asignados = cursoCargos.stream()
				.collect(Collectors.toMap(CursoCargoContratado::getPeriodo, asignado -> asignado));
        log.debug("Asignados -> {}",  Jsonifier.builder(asignados).build());
		// Asociar periodos con cursos
		List<CursoCargoContratado> cargos = new ArrayList<CursoCargoContratado>();
		for (Periodo periodo : periodos) {
			CursoCargoContratado cargo = null;
			if (asignados.containsKey(periodo.getAnho() + "." + periodo.getMes())) {
				cargo = asignados.get(periodo.getAnho() + "." + periodo.getMes());
			} else {
				cargo = new CursoCargoContratado(null,
						cursoId,
						periodo.getAnho(),
						periodo.getMes(),
						contratoId,
                        contrato.getPersonaId(),
                        contrato.getDocumentoId(),
						cargoTipoId,
						horasSemanales,
						BigDecimal.ZERO,
						null,
						null,
						null,
						(byte) 0,
                        null);
			}
            log.debug("Cargo a grabar -> {}",  cargo.jsonify());
			cargos.add(cargo);
		}
		log.debug("ContratoToolService.saveCurso - Cursos a registrar");
        cargos.forEach(cursoCargoContratado -> log.debug("CursoCargoContratado -> {}", cursoCargoContratado.jsonify()));
		cargos = cursoCargoContratadoService.saveAll(cargos);
		log.debug("ContratoToolService.saveCurso - Cursos registrados");
        cargos.forEach(cursoCargoContratado -> log.debug("CursoCargoContratado -> {}", cursoCargoContratado.jsonify()));
		return true;
	}

	@Transactional
	public void generateCurso(Integer anho, Integer mes) {
		for (CursoCargoContratado cargo : cursoCargoContratadoService.findAllByPeriodo(anho, mes)) {
			this.saveCurso(cargo.getCursoId(), cargo.getContratoId(), cargo.getCargoTipoId(),
					cargo.getHorasSemanales());
		}
	}

	@Transactional
	public void deleteCurso(Long cursoCargoContratadoId) {
		CursoCargoContratado cursoCargoContratado = cursoCargoContratadoService
				.findByCursoCargo(cursoCargoContratadoId);
		Long cursoId = cursoCargoContratado.getCursoId();
		Long contratoId = cursoCargoContratado.getContratoId();
		cursoCargoContratadoService.deleteAllByCursoIdAndContratoId(cursoId, contratoId);
	}

}
