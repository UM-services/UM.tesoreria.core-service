/**
 * 
 */
package um.tesoreria.core.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ContratoException;
import um.tesoreria.core.model.Contrato;
import um.tesoreria.core.repository.IContratoRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class ContratoService {

	@Autowired
	private IContratoRepository repository;

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

	public List<Contrato> findAllByContratado(Long contratadoId) {
		return repository.findAllByContratadoId(contratadoId);
	}

	public Contrato findByContratoId(Long contratoId) {
		return repository.findByContratoId(contratoId).orElseThrow(() -> new ContratoException(contratoId));
	}

	public Contrato add(Contrato contrato) {
		contrato = repository.save(contrato);
		log.debug("Contrato -> {}", contrato);
		return contrato;
	}

	public Contrato update(Contrato newContrato, Long contratoId) {
		return repository.findByContratoId(contratoId).map(contrato -> {
			contrato = new Contrato(contratoId, newContrato.getContratadoId(), newContrato.getPersonaId(),
					newContrato.getDocumentoId(), newContrato.getDesde(), newContrato.getFacultadId(),
					newContrato.getPlanId(), newContrato.getMateriaId(), newContrato.getGeograficaId(),
					newContrato.getCargoMateriaId(), newContrato.getPrimerVencimiento(), newContrato.getCargo(),
					newContrato.getMontoFijo(), newContrato.getCanonMensual(), newContrato.getCanonMensualSinAjuste(),
					newContrato.getHasta(), newContrato.getCanonMensualLetras(), newContrato.getCanonTotal(),
					newContrato.getCanonTotalLetras(), newContrato.getMeses(), newContrato.getMesesLetras(),
					newContrato.getAjuste(), null, null, null);
			contrato = repository.save(contrato);
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
