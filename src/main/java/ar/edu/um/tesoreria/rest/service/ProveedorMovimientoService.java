/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import ar.edu.um.tesoreria.rest.exception.ProveedorMovimientoNotFoundException;
import ar.edu.um.tesoreria.rest.model.Comprobante;
import ar.edu.um.tesoreria.rest.model.ProveedorArticulo;
import ar.edu.um.tesoreria.rest.model.ProveedorMovimiento;
import ar.edu.um.tesoreria.rest.repository.IProveedorMovimientoRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class ProveedorMovimientoService {

	@Autowired
	private IProveedorMovimientoRepository repository;

	@Autowired
	private ComprobanteService comprobanteService;

	@Autowired
	private ProveedorArticuloService proveedorArticuloService;

	public List<ProveedorMovimiento> findAllByComprobanteIdAndFechaComprobanteBetween(Integer comprobanteId,
			OffsetDateTime fechaInicio, OffsetDateTime fechaFinal) {
		return repository.findAllByComprobanteIdAndFechaComprobanteBetween(comprobanteId, fechaInicio, fechaFinal);
	}

	public List<ProveedorMovimiento> findAllByProveedorMovimientoIdIn(List<Long> proveedorMovimientoIds) {
		return repository.findAllByProveedorMovimientoIdIn(proveedorMovimientoIds, null);
	}

	public List<ProveedorMovimiento> findAllEliminables(Integer ejercicioId) {
		List<ProveedorMovimiento> anuladas = repository.findAllByComprobanteIdAndPrefijoAndFechaAnulacionNotNull(6,
				ejercicioId);
		List<ProveedorMovimiento> sueldosPendientes = repository
				.findAllByComprobanteIdAndPrefijoAndNetoSinDescuentoAndNombreBeneficiarioStartingWithAndConceptoStartingWith(
						6, ejercicioId, BigDecimal.ZERO, "Personal", "Sueldos");
		return Stream.concat(anuladas.stream(), sueldosPendientes.stream()).collect(Collectors.toList());
	}

	public List<ProveedorMovimiento> findAllByProveedorId(Integer proveedorId, Integer geograficaId) {
		if (geograficaId == 0) {
			return repository.findAllByProveedorId(proveedorId);
		}
		return repository.findAllByProveedorIdAndGeograficaId(proveedorId, geograficaId);
	}

	public List<ProveedorMovimiento> findAllAsignables(Integer proveedorId, OffsetDateTime desde, OffsetDateTime hasta,
			Integer geograficaId, Boolean todos) throws JsonProcessingException {
		List<Comprobante> comprobantes = comprobanteService.findAllByTipoTransaccionId(3);
		log.info("Comprobantes -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter()
				.writeValueAsString(comprobantes));
		List<Integer> comprobanteIds = comprobantes.stream().map(c -> c.getComprobanteId())
				.collect(Collectors.toList());
		log.info("ComprobanteIds -> {}", JsonMapper.builder().findAndAddModules().build()
				.writerWithDefaultPrettyPrinter().writeValueAsString(comprobanteIds));
		List<Long> proveedorMovimientoIds = null;
		if (geograficaId == 0) {
			List<ProveedorMovimiento> proveedorMovimientos = repository
					.findAllByProveedorIdAndFechaComprobanteBetweenAndComprobanteIdInAndFechaAnulacionIsNull(
							proveedorId, desde, hasta, comprobanteIds);
			log.info("ProveedorMovimientos -> {}", JsonMapper.builder().findAndAddModules().build()
					.writerWithDefaultPrettyPrinter().writeValueAsString(proveedorMovimientos));
			proveedorMovimientoIds = proveedorMovimientos.stream().map(m -> m.getProveedorMovimientoId())
					.collect(Collectors.toList());
			log.info("ProveedorMovimientoIds -> {}", JsonMapper.builder().findAndAddModules().build()
					.writerWithDefaultPrettyPrinter().writeValueAsString(proveedorMovimientoIds));
		} else {
			List<ProveedorMovimiento> proveedorMovimientos = repository
					.findAllByProveedorIdAndFechaComprobanteBetweenAndComprobanteIdInAndGeograficaIdAndFechaAnulacionIsNull(
							proveedorId, desde, hasta, comprobanteIds, geograficaId);
			log.info("ProveedorMovimientos -> {}", JsonMapper.builder().findAndAddModules().build()
					.writerWithDefaultPrettyPrinter().writeValueAsString(proveedorMovimientos));
			proveedorMovimientoIds = proveedorMovimientos.stream().map(m -> m.getProveedorMovimientoId())
					.collect(Collectors.toList());
			log.info("ProveedorMovimientoIds -> {}", JsonMapper.builder().findAndAddModules().build()
					.writerWithDefaultPrettyPrinter().writeValueAsString(proveedorMovimientoIds));
		}
		List<ProveedorArticulo> proveedorArticulos = proveedorArticuloService
				.findAllByProveedorMovimientoIds(proveedorMovimientoIds, true);
		log.info("ProveedorArticulos -> {}", JsonMapper.builder().findAndAddModules().build()
				.writerWithDefaultPrettyPrinter().writeValueAsString(proveedorArticulos));
		if (todos) {
			proveedorArticulos = proveedorArticulos.stream()
					.filter(p -> p.getAsignado().compareTo(p.getPrecioFinal()) != 0).collect(Collectors.toList());
			log.info("ProveedorArticulos (if) -> {}", JsonMapper.builder().findAndAddModules().build()
					.writerWithDefaultPrettyPrinter().writeValueAsString(proveedorArticulos));
		}
		proveedorMovimientoIds = proveedorArticulos.stream().map(p -> p.getProveedorMovimientoId())
				.collect(Collectors.toList());
		log.info("ProveedorMovimientoIds -> {}", JsonMapper.builder().findAndAddModules().build()
				.writerWithDefaultPrettyPrinter().writeValueAsString(proveedorMovimientoIds));
		List<ProveedorMovimiento> proveedorMovimientos = repository
				.findAllByProveedorMovimientoIdIn(proveedorMovimientoIds, Sort.by("fechaComprobante").descending()
						.and(Sort.by("prefijo").ascending()).and(Sort.by("numeroComprobante").ascending()));
		log.info("ProveedorMovimientos -> (return) {}", JsonMapper.builder().findAndAddModules().build()
				.writerWithDefaultPrettyPrinter().writeValueAsString(proveedorMovimientos));
		return proveedorMovimientos;
	}

	public ProveedorMovimiento findByProveedorMovimientoId(Long proveedorMovimientoId) {
		return repository.findByProveedorMovimientoId(proveedorMovimientoId)
				.orElseThrow(() -> new ProveedorMovimientoNotFoundException(proveedorMovimientoId));
	}

}
