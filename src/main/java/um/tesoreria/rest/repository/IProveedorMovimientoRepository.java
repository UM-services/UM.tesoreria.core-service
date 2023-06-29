/**
 * 
 */
package um.tesoreria.rest.repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import um.tesoreria.rest.kotlin.model.ProveedorMovimiento;

/**
 * @author daniel
 *
 */
@Repository
public interface IProveedorMovimientoRepository extends JpaRepository<ProveedorMovimiento, Long> {

	public List<ProveedorMovimiento> findAllByComprobanteIdAndFechaComprobanteBetween(Integer comprobanteId,
			OffsetDateTime fechaInicio, OffsetDateTime fechaFinal);

	public List<ProveedorMovimiento> findAllByProveedorMovimientoIdIn(List<Long> proveedorMovimientoIds, Sort sort);

	public List<ProveedorMovimiento> findAllByComprobanteIdAndPrefijoAndFechaAnulacionNotNull(Integer comprobanteId,
			Integer prefijo);

	public List<ProveedorMovimiento> findAllByComprobanteIdAndPrefijoAndNetoSinDescuentoAndNombreBeneficiarioStartingWithAndConceptoStartingWith(
			Integer comprobanteId, Integer prefijo, BigDecimal netoSinDescuento, String nombreBeneficiario,
			String concepto);

	public List<ProveedorMovimiento> findAllByProveedorIdAndFechaComprobanteBetweenAndComprobanteIdInAndFechaAnulacionIsNull(
			Integer proveedorId, OffsetDateTime desde, OffsetDateTime hasta, List<Integer> comprobanteIds);

	public List<ProveedorMovimiento> findAllByProveedorIdAndFechaComprobanteBetweenAndComprobanteIdInAndGeograficaIdAndFechaAnulacionIsNull(
			Integer proveedorId, OffsetDateTime desde, OffsetDateTime hasta, List<Integer> comprobanteIds,
			Integer geograficaId);

	public List<ProveedorMovimiento> findAllByProveedorId(Integer proveedorId);

	public List<ProveedorMovimiento> findAllByProveedorIdAndGeograficaId(Integer proveedorId, Integer geograficaId);

	public Optional<ProveedorMovimiento> findByProveedorMovimientoId(Long proveedorMovimientoId);

	public Optional<ProveedorMovimiento> findByPrefijoAndNumeroComprobanteAndComprobanteIdIn(Integer prefijo, Long numeroComprobante, List<Integer> comprobanteIds);

	@Modifying
    public void deleteByProveedorMovimientoId(Long proveedorMovimientoId);

}
