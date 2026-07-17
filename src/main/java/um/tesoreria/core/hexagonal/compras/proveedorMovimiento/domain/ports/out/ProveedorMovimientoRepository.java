package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface ProveedorMovimientoRepository {

    Optional<ProveedorMovimiento> findById(Long proveedorMovimientoId);

    ProveedorMovimiento save(ProveedorMovimiento proveedorMovimiento);

    void deleteById(Long proveedorMovimientoId);

    void delete(ProveedorMovimiento proveedorMovimiento);

    List<ProveedorMovimiento> findAllByComprobanteIdAndFechaComprobanteBetween(Integer comprobanteId,
                                                                              OffsetDateTime fechaInicio,
                                                                              OffsetDateTime fechaFinal);

    List<ProveedorMovimiento> findAllByIds(List<Long> proveedorMovimientoIds);

    List<ProveedorMovimiento> findAllByComprobanteIdAndPrefijoAndFechaAnulacionNotNull(Integer comprobanteId,
                                                                                        Integer prefijo);

    List<ProveedorMovimiento> findAllByComprobanteIdAndPrefijoAndNetoSinDescuentoAndNombreBeneficiarioStartingWithAndConceptoStartingWith(
            Integer comprobanteId, Integer prefijo, BigDecimal netoSinDescuento,
            String nombreBeneficiario, String concepto);

    List<ProveedorMovimiento> findAllByProveedorId(Integer proveedorId);

    List<ProveedorMovimiento> findAllByProveedorIdAndGeograficaId(Integer proveedorId, Integer geograficaId);

    List<ProveedorMovimiento> findAllByProveedorIdAndFechaComprobanteBetweenAndComprobanteIdInAndFechaAnulacionIsNull(
            Integer proveedorId, OffsetDateTime desde, OffsetDateTime hasta, List<Integer> comprobanteIds);

    List<ProveedorMovimiento> findAllByProveedorIdAndFechaComprobanteBetweenAndComprobanteIdInAndGeograficaIdAndFechaAnulacionIsNull(
            Integer proveedorId, OffsetDateTime desde, OffsetDateTime hasta,
            List<Integer> comprobanteIds, Integer geograficaId);

    List<ProveedorMovimiento> findAllByFechaComprobanteBetweenAndFechaAnulacionIsNullAndComprobanteIdNot(
            OffsetDateTime fechaDesde, OffsetDateTime fechaHasta, Integer comprobanteId);

    Optional<ProveedorMovimiento> findByPrefijoAndNumeroComprobanteAndComprobanteIdIn(
            Integer prefijo, Long numeroComprobante, List<Integer> comprobanteIds);

    Optional<ProveedorMovimiento> findTopByPrefijoAndComprobanteIdInOrderByNumeroComprobanteDesc(
            Integer prefijo, List<Integer> comprobanteIds);

    List<Long> findDistinctProveedorMovimientoIdsForCostAdjustment();

    List<ProveedorMovimiento> findAllByIdsSortedByFechaComprobanteDesc(List<Long> proveedorMovimientoIds);
}
