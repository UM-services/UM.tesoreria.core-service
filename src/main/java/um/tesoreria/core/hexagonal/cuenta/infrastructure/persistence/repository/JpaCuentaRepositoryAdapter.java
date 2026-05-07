package um.tesoreria.core.hexagonal.cuenta.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.cuenta.domain.model.CuentaSearch;
import um.tesoreria.core.hexagonal.cuenta.domain.ports.out.CuentaRepository;
import um.tesoreria.core.hexagonal.cuenta.infrastructure.persistence.entity.CuentaEntity;
import um.tesoreria.core.hexagonal.cuenta.infrastructure.persistence.mapper.CuentaMapper;
import um.tesoreria.core.service.view.CuentaSearchService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaCuentaRepositoryAdapter implements CuentaRepository {

    private final JpaCuentaRepository jpaRepository;
    private final CuentaSearchService searchService;
    private final CuentaMapper mapper;

    @Override
    public Cuenta create(Cuenta cuenta) {
        CuentaEntity entity = mapper.toEntity(cuenta);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Cuenta> findById(BigDecimal numeroCuenta) {
        return jpaRepository.findByNumeroCuenta(numeroCuenta).map(mapper::toDomain);
    }

    @Override
    public Optional<Cuenta> findByCuentaContableId(Long cuentaContableId) {
        return jpaRepository.findByCuentaContableId(cuentaContableId).map(mapper::toDomain);
    }

    @Override
    public List<Cuenta> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Cuenta> findAllByNumeroCuentaIn(List<BigDecimal> cuentas) {
        return jpaRepository.findAllByNumeroCuentaIn(cuentas).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Cuenta> findAllGrado5() {
        return jpaRepository.findAllByGradoAndNumeroCuentaGreaterThan(5, BigDecimal.ZERO, Sort.by("numeroCuenta").ascending())
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Cuenta> findAllByCierreResultado() {
        return jpaRepository.findAllByGradoAndNumeroCuentaBetween(5, new BigDecimal(30000000000L), new BigDecimal(49999999999L))
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Cuenta> findAllByCierreActivoPasivo() {
        return jpaRepository.findAllByGradoAndNumeroCuentaBetween(5, new BigDecimal(10000000000L), new BigDecimal(29999999999L))
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<CuentaSearch> findByStrings(List<String> conditions, Boolean visible) {
        return searchService.findAllByStrings(conditions, visible).stream().map(mapper::toSearchDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Cuenta> update(BigDecimal numeroCuenta, Cuenta cuenta) {
        return jpaRepository.findByNumeroCuenta(numeroCuenta).map(existing -> {
            CuentaEntity entity = mapper.toEntity(cuenta);
            entity.setNumeroCuenta(numeroCuenta);
            return mapper.toDomain(jpaRepository.save(entity));
        });
    }

    @Override
    public void deleteById(BigDecimal numeroCuenta) {
        jpaRepository.deleteByNumeroCuenta(numeroCuenta);
    }

    @Override
    public List<Cuenta> saveAll(List<Cuenta> cuentas) {
        List<CuentaEntity> entities = cuentas.stream().map(mapper::toEntity).collect(Collectors.toList());
        return jpaRepository.saveAll(entities).stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
