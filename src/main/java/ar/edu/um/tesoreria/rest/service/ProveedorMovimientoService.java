/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import ar.edu.um.tesoreria.rest.exception.ProveedorMovimientoException;
import ar.edu.um.tesoreria.rest.model.Comprobante;
import ar.edu.um.tesoreria.rest.model.ProveedorArticulo;
import ar.edu.um.tesoreria.rest.model.ProveedorMovimiento;
import ar.edu.um.tesoreria.rest.model.dto.ProveedorMovimientoDTO;
import ar.edu.um.tesoreria.rest.repository.IProveedorMovimientoRepository;

/**
 * @author daniel
 *
 */
@Service
public class ProveedorMovimientoService {

	@Autowired
	private IProveedorMovimientoRepository repository;

	@Autowired
	private ComprobanteService comprobanteService;

	@Autowired
	private ProveedorArticuloService proveedorArticuloService;

	@Autowired
	private ModelMapper modelMapper;

	public List<ProveedorMovimiento> findAllByComprobanteIdAndFechaComprobanteBetween(Integer comprobanteId,
			OffsetDateTime fechaInicio, OffsetDateTime fechaFinal) {
		return repository.findAllByComprobanteIdAndFechaComprobanteBetween(comprobanteId, fechaInicio, fechaFinal);
	}

	public List<ProveedorMovimiento> findAllByProveedorMovimientoIdIn(List<Long> proveedorMovimientoIds) {
		return repository.findAllByProveedorMovimientoIdIn(proveedorMovimientoIds, null);
	}

	public List<ProveedorMovimientoDTO> findAllEliminables(Integer ejercicioId) {
		List<ProveedorMovimiento> anuladas = repository.findAllByComprobanteIdAndPrefijoAndFechaAnulacionNotNull(6,
				ejercicioId);
		List<ProveedorMovimiento> sueldosPendientes = repository
				.findAllByComprobanteIdAndPrefijoAndNetoSinDescuentoAndNombreBeneficiarioStartingWithAndConceptoStartingWith(
						6, ejercicioId, BigDecimal.ZERO, "Personal", "Sueldos");
		List<ProveedorMovimiento> proveedorMovimientos = Stream.concat(anuladas.stream(), sueldosPendientes.stream())
				.collect(Collectors.toList());
		List<ProveedorMovimientoDTO> proveedorMovimientoDTOs = proveedorMovimientos.stream()
				.map(proveedorMovimiento -> modelMapper.map(proveedorMovimiento, ProveedorMovimientoDTO.class))
				.collect(Collectors.toList());
		return proveedorMovimientoDTOs;
	}

	public List<ProveedorMovimiento> findAllByProveedorId(Integer proveedorId, Integer geograficaId) {
		if (geograficaId == 0) {
			return repository.findAllByProveedorId(proveedorId);
		}
		return repository.findAllByProveedorIdAndGeograficaId(proveedorId, geograficaId);
	}

	public List<ProveedorMovimientoDTO> findAllAsignables(Integer proveedorId, OffsetDateTime desde,
			OffsetDateTime hasta, Integer geograficaId, Boolean todos) throws JsonProcessingException {
		List<Comprobante> comprobantes = comprobanteService.findAllByTipoTransaccionId(3);
		List<Integer> comprobanteIds = comprobantes.stream().map(c -> c.getComprobanteId())
				.collect(Collectors.toList());
		List<Long> proveedorMovimientoIds = null;
		if (geograficaId == 0) {
			List<ProveedorMovimiento> proveedorMovimientos = repository
					.findAllByProveedorIdAndFechaComprobanteBetweenAndComprobanteIdInAndFechaAnulacionIsNull(
							proveedorId, desde, hasta, comprobanteIds);
			proveedorMovimientoIds = proveedorMovimientos.stream().map(m -> m.getProveedorMovimientoId())
					.collect(Collectors.toList());
		} else {
			List<ProveedorMovimiento> proveedorMovimientos = repository
					.findAllByProveedorIdAndFechaComprobanteBetweenAndComprobanteIdInAndGeograficaIdAndFechaAnulacionIsNull(
							proveedorId, desde, hasta, comprobanteIds, geograficaId);
			proveedorMovimientoIds = proveedorMovimientos.stream().map(m -> m.getProveedorMovimientoId())
					.collect(Collectors.toList());
		}
		List<ProveedorArticulo> proveedorArticulos = proveedorArticuloService
				.findAllByProveedorMovimientoIds(proveedorMovimientoIds, true);
		if (todos) {
			proveedorArticulos = proveedorArticulos.stream()
					.filter(p -> p.getAsignado().compareTo(p.getPrecioFinal()) != 0).collect(Collectors.toList());
		}
		proveedorMovimientoIds = proveedorArticulos.stream().map(p -> p.getProveedorMovimientoId())
				.collect(Collectors.toList());
		List<ProveedorMovimiento> proveedorMovimientos = repository
				.findAllByProveedorMovimientoIdIn(proveedorMovimientoIds, Sort.by("fechaComprobante").descending()
						.and(Sort.by("prefijo").ascending()).and(Sort.by("numeroComprobante").ascending()));
		List<ProveedorMovimientoDTO> proveedorMovimientoDTOs = proveedorMovimientos.stream()
				.map(proveedorMovimiento -> modelMapper.map(proveedorMovimiento, ProveedorMovimientoDTO.class))
				.collect(Collectors.toList());
		return proveedorMovimientoDTOs;
	}

	public ProveedorMovimientoDTO findByProveedorMovimientoId(Long proveedorMovimientoId) {
		ProveedorMovimiento proveedorMovimiento = repository.findByProveedorMovimientoId(proveedorMovimientoId)
				.orElseThrow(() -> new ProveedorMovimientoException(proveedorMovimientoId));
		return modelMapper.map(proveedorMovimiento, ProveedorMovimientoDTO.class);
	}

}
