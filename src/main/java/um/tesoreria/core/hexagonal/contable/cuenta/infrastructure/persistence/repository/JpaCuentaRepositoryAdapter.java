package um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.contable.cuenta.application.exception.CuentaConflictoException;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.CuentaJerarquia;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.CuentaSearch;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaRepository;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.entity.CuentaEntity;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.mapper.CuentaMapper;
import um.tesoreria.core.service.view.CuentaSearchService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaCuentaRepositoryAdapter implements CuentaRepository {

    private final JpaCuentaRepository jpaRepository;
    private final CuentaSearchService searchService;
    private final CuentaMapper mapper;
    private final CuentaMovimientoReferenciaRepository movimientos;
    private final EntityManager entityManager;

    @Override
    public Cuenta create(Cuenta cuenta) {
        CuentaEntity entity = mapper.toEntity(cuenta);
        // persist obliga a INSERT: save haría merge y podría pisar una cuenta en una carrera.
        entityManager.persist(entity);
        entityManager.flush();
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<Cuenta> findWithLockByNumeroCuenta(BigDecimal numeroCuenta) {
        return jpaRepository.findWithLockByNumeroCuenta(numeroCuenta).map(mapper::toDomain);
    }

    @Override
    public boolean existenMovimientos(BigDecimal desde, BigDecimal hasta) {
        return movimientos.existsByNumeroCuentaBetween(desde, hasta);
    }

    @Override
    public boolean existenHijas(BigDecimal desde, BigDecimal hasta) {
        return jpaRepository.existsByNumeroCuentaGreaterThanAndNumeroCuentaLessThanEqual(desde, hasta);
    }

    @Override
    public List<Cuenta> findAllByGeograficaIdAndGrado(Integer geograficaId, Integer grado) {
        return jpaRepository.findAllByGeograficaIdAndGradoOrderByNumeroCuenta(geograficaId, grado)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void recalcularGrados() {
        for (CuentaEntity entity : jpaRepository.findAll()) {
            final CuentaJerarquia.Grados grados;
            try {
                grados = CuentaJerarquia.grados(entity.getNumeroCuenta());
            } catch (IllegalArgumentException ex) {
                // Un número legado fuera de la estructura conserva sus grados: no debe cortar el balance.
                continue;
            }
            entity.setGrado(grados.grado());
            entity.setGrado1(grados.grado1());
            entity.setGrado2(grados.grado2());
            entity.setGrado3(grados.grado3());
            entity.setGrado4(grados.grado4());
        }
        // Dirty checking sobre entidades gestionadas: no merge ni INSERT de filas borradas.
        jpaRepository.flush();
    }

    @Override
    @Transactional
    public void limpiarNombres() {
        // Leer el nombre vigente con bloqueo y conservarlo hasta el flush/commit.
        // El orden numérico coincide con el de los bloqueos de alta/edición/baja.
        for (CuentaEntity entity : jpaRepository.findAllByNombreContainingOrderByNumeroCuenta("\r\n")) {
            entity.setNombre(entity.getNombre().replace("\r\n", ""));
        }
        jpaRepository.flush();
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
            existing.setNombre(cuenta.getNombre());
            existing.setIntegradora(cuenta.getIntegradora());
            existing.setGrado(cuenta.getGrado());
            existing.setGrado1(cuenta.getGrado1());
            existing.setGrado2(cuenta.getGrado2());
            existing.setGrado3(cuenta.getGrado3());
            existing.setGrado4(cuenta.getGrado4());
            existing.setGeograficaId(cuenta.getGeograficaId());
            existing.setFechaBloqueo(cuenta.getFechaBloqueo());
            existing.setVisible(cuenta.getVisible());
            return mapper.toDomain(existing);
        });
    }

    @Override
    public void deleteById(BigDecimal numeroCuenta) {
        try {
            jpaRepository.deleteByNumeroCuenta(numeroCuenta);
            jpaRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new CuentaConflictoException("La cuenta " + numeroCuenta + " tiene registros asociados");
        }
    }

    @Override
    public List<Cuenta> saveAll(List<Cuenta> cuentas) {
        List<CuentaEntity> entities = cuentas.stream().map(mapper::toEntity).collect(Collectors.toList());
        return jpaRepository.saveAll(entities).stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
