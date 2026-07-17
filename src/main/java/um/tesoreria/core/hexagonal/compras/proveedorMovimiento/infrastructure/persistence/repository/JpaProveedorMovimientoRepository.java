/**
 * 
 */
package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.entity.ProveedorMovimientoEntity;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaProveedorMovimientoRepository extends JpaRepository<ProveedorMovimientoEntity, Long> {

	List<ProveedorMovimientoEntity> findAllByComprobanteIdAndFechaComprobanteBetween(Integer comprobanteId,
	                                                                                 OffsetDateTime fechaInicio, OffsetDateTime fechaFinal);

	List<ProveedorMovimientoEntity> findAllByProveedorMovimientoIdIn(List<Long> proveedorMovimientoIds, Sort sort);

	List<ProveedorMovimientoEntity> findAllByComprobanteIdAndPrefijoAndFechaAnulacionNotNull(Integer comprobanteId,
	                                                                                         Integer prefijo);

	List<ProveedorMovimientoEntity> findAllByComprobanteIdAndPrefijoAndNetoSinDescuentoAndNombreBeneficiarioStartingWithAndConceptoStartingWith(
			Integer comprobanteId, Integer prefijo, BigDecimal netoSinDescuento, String nombreBeneficiario,
			String concepto);

	List<ProveedorMovimientoEntity> findAllByProveedorIdAndFechaComprobanteBetweenAndComprobanteIdInAndFechaAnulacionIsNull(
			Integer proveedorId, OffsetDateTime desde, OffsetDateTime hasta, List<Integer> comprobanteIds);

	List<ProveedorMovimientoEntity> findAllByProveedorIdAndFechaComprobanteBetweenAndComprobanteIdInAndGeograficaIdAndFechaAnulacionIsNull(
			Integer proveedorId, OffsetDateTime desde, OffsetDateTime hasta, List<Integer> comprobanteIds,
			Integer geograficaId);

	List<ProveedorMovimientoEntity> findAllByProveedorId(Integer proveedorId);

	List<ProveedorMovimientoEntity> findAllByProveedorIdAndGeograficaId(Integer proveedorId, Integer geograficaId);

	List<ProveedorMovimientoEntity> findAllByFechaComprobanteBetweenAndFechaAnulacionIsNullAndComprobanteIdNotOrderByNombreBeneficiario(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta, Integer comprobanteId);

	Optional<ProveedorMovimientoEntity> findByProveedorMovimientoId(Long proveedorMovimientoId);

	Optional<ProveedorMovimientoEntity> findByPrefijoAndNumeroComprobanteAndComprobanteIdIn(Integer prefijo, Long numeroComprobante, List<Integer> comprobanteIds);

	Optional<ProveedorMovimientoEntity> findTopByPrefijoAndComprobanteIdInOrderByNumeroComprobanteDesc(Integer prefijo, List<Integer> comprobanteIds);

	@Modifying
    void deleteByProveedorMovimientoId(Long proveedorMovimientoId);

	// Revisar antes de usar
	@Query(value = """
            SELECT DISTINCT m.mvp_id 
            FROM movprov m
            JOIN movprov_detallefactura d ON m.MvP_ID = d.FaD_MvP_ID
            JOIN entrega_detalle ed ON d.FaD_ID = ed.proveedor_articulo_id
            JOIN entrega e ON ed.NeD_NoE_ID = e.NoE_ID
            JOIN movcon c ON e.NoE_MCo_Fecha = c.MCo_Fecha AND e.NoE_MCo_NroComp = c.MCo_NroComp
            WHERE m.MvP_FechaComprob >= '20230301'
              AND e.NoE_MCo_Fecha >= '20230301'
              AND e.NoE_Anulada = 0
              AND m.MvP_FechaComprob != e.NoE_MCo_Fecha
            ORDER BY m.MvP_ID
            """, nativeQuery = true)
	List<Long> findDistinctProveedorMovimientoIdsForCostAdjustment();

}
