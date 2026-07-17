package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.mapper.CuentaMovimientoMapper;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.repository.JpaCuentaMovimientoRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaCuentaMovimientoRepositoryAdapter implements CuentaMovimientoRepository {

    private final JpaCuentaMovimientoRepository jpaCuentaMovimientoRepository;
    private final CuentaMovimientoMapper cuentaMovimientoMapper;

    @Override
    public List<CuentaMovimiento> findAllByNumeroCuenta(BigDecimal numeroCuenta) {
        return jpaCuentaMovimientoRepository.findAllByNumeroCuenta(numeroCuenta).stream()
                .map(cuentaMovimientoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CuentaMovimiento> findTopByNumeroCuenta(BigDecimal numeroCuenta) {
        return jpaCuentaMovimientoRepository.findTopByNumeroCuenta(numeroCuenta)
                .map(cuentaMovimientoMapper::toDomainModel);
    }

    @Override
    public List<CuentaMovimiento> findAllByFechaContableAndOrdenContableAndItemGreaterThanEqual(
            OffsetDateTime fechaContable, Integer ordenContable, Integer item) {
        return jpaCuentaMovimientoRepository
                .findAllByFechaContableAndOrdenContableAndItemGreaterThanEqual(
                        fechaContable, ordenContable, item,
                        Sort.by("debita").descending().and(Sort.by("item").ascending())).stream()
                .map(cuentaMovimientoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<CuentaMovimiento> findAllByFechaContableAndOrdenContableAndDebitaAndItemGreaterThanEqual(
            OffsetDateTime fechaContable, Integer ordenContable, Byte debita, Integer item) {
        return jpaCuentaMovimientoRepository
                .findAllByFechaContableAndOrdenContableAndDebitaAndItemGreaterThanEqual(
                        fechaContable, ordenContable, debita, item,
                        Sort.by("debita").descending().and(Sort.by("item").ascending())).stream()
                .map(cuentaMovimientoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<CuentaMovimiento> findAllByNumeroCuentaAndFechaContableBetweenAndApertura(
            BigDecimal numeroCuenta, OffsetDateTime desde, OffsetDateTime hasta, Byte apertura) {
        return jpaCuentaMovimientoRepository
                .findAllByNumeroCuentaAndFechaContableBetweenAndApertura(
                        numeroCuenta, desde, hasta, apertura,
                        Sort.by("fechaContable").ascending().and(Sort.by("ordenContable").ascending())).stream()
                .map(cuentaMovimientoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CuentaMovimiento> findTopByFechaContableOrderByOrdenContableDesc(OffsetDateTime fechaContable) {
        return jpaCuentaMovimientoRepository.findTopByFechaContableOrderByOrdenContableDesc(fechaContable)
                .map(cuentaMovimientoMapper::toDomainModel);
    }

    @Override
    public Optional<CuentaMovimiento> findByCuentaMovimientoId(Long cuentaMovimientoId) {
        return jpaCuentaMovimientoRepository.findByCuentaMovimientoId(cuentaMovimientoId)
                .map(cuentaMovimientoMapper::toDomainModel);
    }

    @Override
    public CuentaMovimiento save(CuentaMovimiento cuentaMovimiento) {
        var entity = cuentaMovimientoMapper.toEntity(cuentaMovimiento);
        var saved = jpaCuentaMovimientoRepository.save(entity);
        return cuentaMovimientoMapper.toDomainModel(saved);
    }

    @Override
    public void deleteAllByFechaContableAndOrdenContable(OffsetDateTime fechaContable, Integer ordenContable) {
        jpaCuentaMovimientoRepository.deleteAllByFechaContableAndOrdenContable(fechaContable, ordenContable);
    }

    @Override
    public void deleteByCuentaMovimientoId(Long cuentaMovimientoId) {
        jpaCuentaMovimientoRepository.deleteByCuentaMovimientoId(cuentaMovimientoId);
    }

    @Override
    public void deleteAllByCuentaMovimientoIdIn(List<Long> cuentaMovimientoIds) {
        jpaCuentaMovimientoRepository.deleteAllByCuentaMovimientoIdIn(cuentaMovimientoIds);
    }
}
