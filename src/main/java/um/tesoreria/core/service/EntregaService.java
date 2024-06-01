/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import um.tesoreria.core.exception.EntregaException;
import um.tesoreria.core.kotlin.model.Entrega;
import um.tesoreria.core.repository.IEntregaRepository;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class EntregaService {

	@Resource
	private IEntregaRepository repository;

	@Resource
	private EntregaDetalleService entregaDetalleService;

	public List<Entrega> findAllDetalleByProveedorMovimientoId(Long proveedorMovimientoId, Boolean soloActivas)
			throws JsonProcessingException {
		List<Long> entregaIds = entregaDetalleService.findAllByProveedorMovimientoId(proveedorMovimientoId).stream()
				.map(d -> d.getEntregaId()).collect(Collectors.toList());
		if (soloActivas) {
			List<Entrega> entregas = repository.findAllByEntregaIdInAndAnulada(entregaIds, (byte) 0,
					Sort.by("fechaContable").ascending().and(Sort.by("ordenContable").ascending()));
			log.info("Entregas -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter()
					.writeValueAsString(entregas));
			return entregas;
		}
		List<Entrega> entregas = repository.findAllByEntregaIdIn(entregaIds,
				Sort.by("fechaContable").ascending().and(Sort.by("ordenContable").ascending()));
		log.info("Entregas -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter()
				.writeValueAsString(entregas));
		return entregas;
	}

	public List<Entrega> findAllDetalleByProveedorMovimientoIds(List<Long> proveedorMovimientoIds,
			Boolean soloActivas) throws JsonProcessingException {
		List<Long> entregaIds = entregaDetalleService.findAllByProveedorMovimientoIds(proveedorMovimientoIds).stream()
				.map(d -> d.getEntregaId()).collect(Collectors.toList());
		if (soloActivas) {
			List<Entrega> entregas = repository.findAllByEntregaIdInAndAnulada(entregaIds, (byte) 0,
					Sort.by("fechaContable").ascending().and(Sort.by("ordenContable").ascending()));
			log.info("Entregas -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter()
					.writeValueAsString(entregas));
			return entregas;
		}
		List<Entrega> entregas = repository.findAllByEntregaIdIn(entregaIds,
				Sort.by("fechaContable").ascending().and(Sort.by("ordenContable").ascending()));
		log.info("Entregas -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter()
				.writeValueAsString(entregas));
		return entregas;
	}

	public Entrega findByEntregaId(Long entregaId) {
		return repository.findByEntregaId(entregaId).orElseThrow(() -> new EntregaException(entregaId));
	}

	public Entrega add(Entrega entrega) {
		entrega = repository.save(entrega);
		return entrega;
	}

	public Entrega update(Entrega newEntrega, Long entregaId) {
		return repository.findByEntregaId(entregaId).map(entrega -> {
			entrega = new Entrega.Builder()
					.entregaId(entregaId)
					.fecha(newEntrega.getFecha())
					.ubicacionId(newEntrega.getUbicacionId())
					.recibe(newEntrega.getRecibe())
					.observacion(newEntrega.getObservacion())
					.fechaContable(newEntrega.getFechaContable())
					.ordenContable(newEntrega.getOrdenContable())
					.anulada(newEntrega.getAnulada())
					.tipo(newEntrega.getTipo())
					.trackId(newEntrega.getTrackId())
					.build();
			return repository.save(entrega);
		}).orElseThrow(() -> new EntregaException(entregaId));
	}

	@Transactional
	public void deleteByEntregaId(Long entregaId) {
		repository.deleteByEntregaId(entregaId);
	}

}
