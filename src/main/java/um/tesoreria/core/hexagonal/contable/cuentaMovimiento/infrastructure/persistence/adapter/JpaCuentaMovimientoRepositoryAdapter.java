package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.LineaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.SumaCuenta;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.mapper.CuentaMovimientoMapper;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.repository.JpaCuentaMovimientoRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaCuentaMovimientoRepositoryAdapter implements CuentaMovimientoRepository {

    private final JpaCuentaMovimientoRepository jpaCuentaMovimientoRepository;
    private final CuentaMovimientoMapper cuentaMovimientoMapper;

    @Override
    public boolean existsByFechaContableBetween(OffsetDateTime desde, OffsetDateTime hasta) {
        return jpaCuentaMovimientoRepository.existsByFechaContableBetween(desde, hasta);
    }

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
    public List<CuentaMovimiento> findAllByProveedorMovimientoId(Long proveedorMovimientoId) {
        return jpaCuentaMovimientoRepository
                .findAllByProveedorMovimientoId(proveedorMovimientoId,
                        Sort.by("debita").descending().and(Sort.by("fechaContable").ascending())
                                .and(Sort.by("ordenContable").ascending()).and(Sort.by("item").ascending())).stream()
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
    public List<LineaMovimiento> findLineasByFechaContable(OffsetDateTime desde, OffsetDateTime hasta, int limite) {
        Sort orden = Sort.by("fechaContable", "ordenContable", "item", "cuentaMovimientoId").ascending();
        return jpaCuentaMovimientoRepository
                .findAllByFechaContableGreaterThanEqualAndFechaContableLessThan(desde, hasta, orden, Limit.of(limite)).stream()
                .map(l -> new LineaMovimiento(l.getCuentaMovimientoId(), l.getFechaContable(), l.getOrdenContable(),
                        l.getItem(), l.getNumeroCuenta(), l.getDebita(), l.getImporte(), l.getConcepto(),
                        l.getComprobanteId(), l.getProveedorId(), l.getApertura()))
                .collect(Collectors.toList());
    }

    @Override
    public long countByFechaContable(OffsetDateTime desde, OffsetDateTime hasta) {
        return jpaCuentaMovimientoRepository.countByFechaContableGreaterThanEqualAndFechaContableLessThan(desde, hasta);
    }

    @Override
    public List<SumaCuenta> sumarPorCuenta(OffsetDateTime desde, OffsetDateTime hasta, Byte apertura) {
        // debita = 1 es debe y debita = 0 es haber, igual que ContabilidadService.saldoPeriodo; otro valor no suma.
        Map<BigDecimal, BigDecimal[]> acumulado = new LinkedHashMap<>();
        for (var movimiento : jpaCuentaMovimientoRepository.findAllByFechaContableBetweenAndApertura(desde, hasta, apertura)) {
            BigDecimal importe = movimiento.getImporte() == null ? BigDecimal.ZERO : movimiento.getImporte();
            BigDecimal[] par = acumulado.computeIfAbsent(movimiento.getNumeroCuenta(),
                    cuenta -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            if (movimiento.getDebita() == 1) {
                par[0] = par[0].add(importe);
            } else if (movimiento.getDebita() == 0) {
                par[1] = par[1].add(importe);
            }
        }
        return acumulado.entrySet().stream()
                .map(e -> new SumaCuenta(e.getKey(), e.getValue()[0], e.getValue()[1]))
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