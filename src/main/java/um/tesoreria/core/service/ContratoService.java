/**
 * 
 */
package um.tesoreria.core.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ContratoException;
import um.tesoreria.core.model.Contrato;
import um.tesoreria.core.repository.ContratoRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ContratoService {

	private final ContratoRepository repository;

	public List<Contrato> findAllByFacultad(Integer facultadId, Integer geograficaId) {
		return repository.findAllByFacultadIdAndGeograficaId(facultadId, geograficaId);
	}

	public List<Contrato> findAllAjustable(OffsetDateTime referencia) {
		return repository.findAllByAjusteAndHastaGreaterThanEqual((byte) 1, referencia);
	}

	public List<Contrato> findAllVigente(OffsetDateTime referencia) {
		return repository.findAllByHastaGreaterThanEqual(referencia);
	}

	public List<Contrato> findAllByPersona(BigDecimal personaId, Integer documentoId) {
		return repository.findAllByPersonaIdAndDocumentoIdOrderByDesdeDesc(personaId, documentoId);
	}

	public Contrato findByContratoId(Long contratoId) {
		return repository.findByContratoId(contratoId).orElseThrow(() -> new ContratoException(contratoId));
	}

	public Contrato add(Contrato contrato) {
		contrato = repository.save(contrato);
		log.debug("Contrato -> {}", contrato.jsonify());
		return contrato;
	}

	public Contrato update(Contrato newContrato, Long contratoId) {
		return repository.findByContratoId(contratoId).map(contrato -> {
            contrato.setPersonaId(newContrato.getPersonaId());
            contrato.setDocumentoId(newContrato.getDocumentoId());
            contrato.setDesde(newContrato.getDesde());
            contrato.setHasta(newContrato.getHasta());
            contrato.setFacultadId(newContrato.getFacultadId());
            contrato.setPlanId(newContrato.getPlanId());
            contrato.setMateriaId(newContrato.getMateriaId());
            contrato.setGeograficaId(newContrato.getGeograficaId());
            contrato.setCargoMateriaId(newContrato.getCargoMateriaId());
            contrato.setPrimerVencimiento(newContrato.getPrimerVencimiento());
            contrato.setCargo(newContrato.getCargo());
            contrato.setMontoFijo(newContrato.getMontoFijo());
            contrato.setCanonMensual(newContrato.getCanonMensual());
            contrato.setCanonMensualSinAjuste(newContrato.getCanonMensualSinAjuste());
            contrato.setCanonMensualLetras(newContrato.getCanonMensualLetras());
            contrato.setCanonTotal(newContrato.getCanonTotal());
            contrato.setCanonTotalLetras(newContrato.getCanonTotalLetras());
            contrato.setMeses(newContrato.getMeses());
            contrato.setMesesLetras(newContrato.getMesesLetras());
            contrato.setAjuste(newContrato.getAjuste());
			contrato = repository.save(contrato);
            log.debug("Contrato -> {}", contrato.jsonify());
			return contrato;
		}).orElseThrow(() -> new ContratoException(contratoId));
	}

	public List<Contrato> saveAll(List<Contrato> contratos) {
		contratos = repository.saveAll(contratos);
		return contratos;
	}

	@Transactional
	public void deleteByContratoId(Long contratoId) {
		repository.deleteByContratoId(contratoId);
	}

}
