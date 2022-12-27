/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ContratoFacturaNotFoundException;
import ar.edu.um.tesoreria.rest.model.Contrato;
import ar.edu.um.tesoreria.rest.model.ContratoFactura;
import ar.edu.um.tesoreria.rest.repository.IContratoFacturaRepository;

/**
 * @author daniel
 *
 */
@Service
public class ContratoFacturaService {

	@Autowired
	private IContratoFacturaRepository repository;

	@Autowired
	private ContratoService contratoservice;

	public List<ContratoFactura> findAllPendienteByFacultad(Integer facultadId, Integer geograficaId) {
		return repository.findAllByPendienteAndContratoIdIn((byte) 1,
				contratoservice.findAllByFacultad(facultadId, geograficaId).stream()
						.map(contrato -> contrato.getContratoId()).collect(Collectors.toList()));
	}

	public List<ContratoFactura> findAllPendienteByPersona(BigDecimal personaId, Integer documentoId) {
		return repository.findAllByPendienteAndContratoIdIn((byte) 1,
				contratoservice.findAllByPersona(personaId, documentoId).stream()
						.map(contrato -> contrato.getContratoId()).collect(Collectors.toList()));
	}

	public List<ContratoFactura> findAllExcluidoByPersona(BigDecimal personaId, Integer documentoId) {
		return repository.findAllByExcluidoAndContratoIdIn((byte) 1,
				contratoservice.findAllByPersona(personaId, documentoId).stream()
						.map(contrato -> contrato.getContratoId()).collect(Collectors.toList()));
	}

	public List<ContratoFactura> findAllByContrato(Long contratoId) {
		return repository.findAllByContratoIdOrderByFechaDesc(contratoId);
	}

	public List<ContratoFactura> findAllByEnvio(OffsetDateTime fecha, Integer envio) {
		return repository.findAllByAcreditacionAndEnvio(fecha, envio);
	}

	public ContratoFactura findByContratofacturaId(Long contratofacturaId) {
		return repository.findByContratofacturaId(contratofacturaId)
				.orElseThrow(() -> new ContratoFacturaNotFoundException(contratofacturaId));
	}

	public ContratoFactura findLastByPersona(BigDecimal personaId, Integer documentoId) {
		List<Contrato> contratos = contratoservice.findAllByPersona(personaId, documentoId);
		List<Long> contratoIds = contratos.stream().map(contrato -> contrato.getContratoId())
				.collect(Collectors.toList());
		return repository.findFirstByContratoIdInAndCbuNotOrderByFechaDesc(contratoIds, "")
				.orElseThrow(() -> new ContratoFacturaNotFoundException(personaId, documentoId));
	}

	public ContratoFactura add(ContratoFactura contratofactura) {
		contratofactura = repository.save(contratofactura);
		return contratofactura;
	}

	public ContratoFactura update(ContratoFactura newcontratofactura, Long contratofacturaId) {
		return repository.findByContratofacturaId(contratofacturaId).map(contratofactura -> {
			contratofactura = new ContratoFactura(contratofacturaId, newcontratofactura.getContratoId(),
					newcontratofactura.getComprobanteId(), newcontratofactura.getPrefijo(),
					newcontratofactura.getNumerocomprobante(), newcontratofactura.getFecha(),
					newcontratofactura.getImporte(), newcontratofactura.getPendiente(),
					newcontratofactura.getExcluido(), newcontratofactura.getAcreditacion(),
					newcontratofactura.getEnvio(), newcontratofactura.getCbu());
			contratofactura = repository.save(contratofactura);
			return contratofactura;
		}).orElseThrow(() -> new ContratoFacturaNotFoundException(contratofacturaId));
	}

	public List<ContratoFactura> saveAll(List<ContratoFactura> facturas) {
		facturas = repository.saveAll(facturas);
		return facturas;
	}

	@Transactional
	public void deleteByContratofacturaId(Long contratofacturaId) {
		repository.deleteByContratofacturaId(contratofacturaId);

	}

}
