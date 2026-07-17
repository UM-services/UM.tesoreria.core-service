package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.entity.ProveedorMovimientoEntity;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.mapper.ProveedorMovimientoMapper;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.repository.JpaProveedorMovimientoRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaProveedorMovimientoRepositoryAdapter implements ProveedorMovimientoRepository {

    private final JpaProveedorMovimientoRepository jpaProveedorMovimientoRepository;
    private final ProveedorMovimientoMapper proveedorMovimientoMapper;

    @Override
    public Optional<ProveedorMovimiento> findById(Long proveedorMovimientoId) {
        return jpaProveedorMovimientoRepository.findByProveedorMovimientoId(proveedorMovimientoId)
                .map(proveedorMovimientoMapper::toDomainModel);
    }

    @Override
    public ProveedorMovimiento save(ProveedorMovimiento proveedorMovimiento) {
        ProveedorMovimientoEntity entity = proveedorMovimientoMapper.toEntity(proveedorMovimiento);
        ProveedorMovimientoEntity saved = jpaProveedorMovimientoRepository.save(entity);
        return proveedorMovimientoMapper.toDomainModel(saved);
    }

    @Override
    public void deleteById(Long proveedorMovimientoId) {
        jpaProveedorMovimientoRepository.deleteByProveedorMovimientoId(proveedorMovimientoId);
    }

    @Override
    public void delete(ProveedorMovimiento proveedorMovimiento) {
        jpaProveedorMovimientoRepository.delete(proveedorMovimientoMapper.toEntity(proveedorMovimiento));
    }

    @Override
    public List<ProveedorMovimiento> findAllByComprobanteIdAndFechaComprobanteBetween(
            Integer comprobanteId, OffsetDateTime fechaInicio, OffsetDateTime fechaFinal) {
        return jpaProveedorMovimientoRepository
                .findAllByComprobanteIdAndFechaComprobanteBetween(comprobanteId, fechaInicio, fechaFinal)
                .stream()
                .map(proveedorMovimientoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProveedorMovimiento> findAllByIds(List<Long> proveedorMovimientoIds) {
        return jpaProveedorMovimientoRepository
                .findAllByProveedorMovimientoIdIn(proveedorMovimientoIds, null)
                .stream()
                .map(proveedorMovimientoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProveedorMovimiento> findAllByIdsSortedByFechaComprobanteDesc(List<Long> proveedorMovimientoIds) {
        return jpaProveedorMovimientoRepository
                .findAllByProveedorMovimientoIdIn(proveedorMovimientoIds,
                        Sort.by("fechaComprobante").descending()
                                .and(Sort.by("prefijo").ascending())
                                .and(Sort.by("numeroComprobante").ascending()))
                .stream()
                .map(proveedorMovimientoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProveedorMovimiento> findAllByComprobanteIdAndPrefijoAndFechaAnulacionNotNull(
            Integer comprobanteId, Integer prefijo) {
        return jpaProveedorMovimientoRepository
                .findAllByComprobanteIdAndPrefijoAndFechaAnulacionNotNull(comprobanteId, prefijo)
                .stream()
                .map(proveedorMovimientoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProveedorMovimiento> findAllByComprobanteIdAndPrefijoAndNetoSinDescuentoAndNombreBeneficiarioStartingWithAndConceptoStartingWith(
            Integer comprobanteId, Integer prefijo, BigDecimal netoSinDescuento,
            String nombreBeneficiario, String concepto) {
        return jpaProveedorMovimientoRepository
                .findAllByComprobanteIdAndPrefijoAndNetoSinDescuentoAndNombreBeneficiarioStartingWithAndConceptoStartingWith(
                        comprobanteId, prefijo, netoSinDescuento, nombreBeneficiario, concepto)
                .stream()
                .map(proveedorMovimientoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProveedorMovimiento> findAllByProveedorId(Integer proveedorId) {
        return jpaProveedorMovimientoRepository.findAllByProveedorId(proveedorId)
                .stream()
                .map(proveedorMovimientoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProveedorMovimiento> findAllByProveedorIdAndGeograficaId(Integer proveedorId, Integer geograficaId) {
        return jpaProveedorMovimientoRepository.findAllByProveedorIdAndGeograficaId(proveedorId, geograficaId)
                .stream()
                .map(proveedorMovimientoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProveedorMovimiento> findAllByProveedorIdAndFechaComprobanteBetweenAndComprobanteIdInAndFechaAnulacionIsNull(
            Integer proveedorId, OffsetDateTime desde, OffsetDateTime hasta, List<Integer> comprobanteIds) {
        return jpaProveedorMovimientoRepository
                .findAllByProveedorIdAndFechaComprobanteBetweenAndComprobanteIdInAndFechaAnulacionIsNull(
                        proveedorId, desde, hasta, comprobanteIds)
                .stream()
                .map(proveedorMovimientoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProveedorMovimiento> findAllByProveedorIdAndFechaComprobanteBetweenAndComprobanteIdInAndGeograficaIdAndFechaAnulacionIsNull(
            Integer proveedorId, OffsetDateTime desde, OffsetDateTime hasta,
            List<Integer> comprobanteIds, Integer geograficaId) {
        return jpaProveedorMovimientoRepository
                .findAllByProveedorIdAndFechaComprobanteBetweenAndComprobanteIdInAndGeograficaIdAndFechaAnulacionIsNull(
                        proveedorId, desde, hasta, comprobanteIds, geograficaId)
                .stream()
                .map(proveedorMovimientoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProveedorMovimiento> findAllByFechaComprobanteBetweenAndFechaAnulacionIsNullAndComprobanteIdNot(
            OffsetDateTime fechaDesde, OffsetDateTime fechaHasta, Integer comprobanteId) {
        return jpaProveedorMovimientoRepository
                .findAllByFechaComprobanteBetweenAndFechaAnulacionIsNullAndComprobanteIdNotOrderByNombreBeneficiario(
                        fechaDesde, fechaHasta, comprobanteId)
                .stream()
                .map(proveedorMovimientoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProveedorMovimiento> findByPrefijoAndNumeroComprobanteAndComprobanteIdIn(
            Integer prefijo, Long numeroComprobante, List<Integer> comprobanteIds) {
        return jpaProveedorMovimientoRepository
                .findByPrefijoAndNumeroComprobanteAndComprobanteIdIn(prefijo, numeroComprobante, comprobanteIds)
                .map(proveedorMovimientoMapper::toDomainModel);
    }

    @Override
    public Optional<ProveedorMovimiento> findTopByPrefijoAndComprobanteIdInOrderByNumeroComprobanteDesc(
            Integer prefijo, List<Integer> comprobanteIds) {
        return jpaProveedorMovimientoRepository
                .findTopByPrefijoAndComprobanteIdInOrderByNumeroComprobanteDesc(prefijo, comprobanteIds)
                .map(proveedorMovimientoMapper::toDomainModel);
    }

    @Override
    public List<Long> findDistinctProveedorMovimientoIdsForCostAdjustment() {
        return jpaProveedorMovimientoRepository.findDistinctProveedorMovimientoIdsForCostAdjustment();
    }
}
