/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.EntregaNotFoundException;
import ar.edu.um.tesoreria.rest.model.Entrega;
import ar.edu.um.tesoreria.rest.repository.IEntregaRepository;

/**
 * @author daniel
 *
 */
@Service
public class EntregaService {

	@Autowired
	private IEntregaRepository repository;

	@Autowired
	private EntregaDetalleService entregaDetalleService;

	public List<Entrega> findAllDetalleByProveedorMovimientoId(Long proveedorMovimientoId, Boolean soloActivas) {
		List<Long> entregaIds = entregaDetalleService.findAllByProveedorMovimientoId(proveedorMovimientoId).stream()
				.map(d -> d.getEntregaId()).collect(Collectors.toList());
		if (soloActivas) {
			return repository.findAllByEntregaIdInAndAnulada(entregaIds, (byte) 0,
					Sort.by("fechaContable").ascending().and(Sort.by("ordenContable").ascending()));
		}
		return repository.findAllByEntregaIdIn(entregaIds,
				Sort.by("fechaContable").ascending().and(Sort.by("ordenContable").ascending()));
	}

	public Entrega findByEntregaId(Long entregaId) {
		return repository.findByEntregaId(entregaId).orElseThrow(() -> new EntregaNotFoundException(entregaId));
	}

	public Entrega add(Entrega entrega) {
		entrega = repository.save(entrega);
		return entrega;
	}

	@Transactional
	public void deleteByEntregaId(Long entregaId) {
		repository.deleteByEntregaId(entregaId);
	}
}
