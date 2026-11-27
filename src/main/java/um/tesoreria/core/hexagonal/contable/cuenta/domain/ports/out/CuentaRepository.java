package um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out;

import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.CuentaSearch;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CuentaRepository {
    Cuenta create(Cuenta cuenta);
    Optional<Cuenta> findWithLockByNumeroCuenta(BigDecimal numeroCuenta);
    boolean existenMovimientos(BigDecimal desde, BigDecimal hasta);
    boolean existenHijas(BigDecimal desde, BigDecimal hasta);
    int recalcularGrados();
    int limpiarNombres();
    List<Cuenta> findAllByGeograficaIdAndGrado(Integer geograficaId, Integer grado);
    Optional<Cuenta> findById(BigDecimal numeroCuenta);
    Optional<Cuenta> findByCuentaContableId(Long cuentaContableId);
    List<Cuenta> findAll();
    List<Cuenta> findAllByNumeroCuentaIn(List<BigDecimal> cuentas);
    List<Cuenta> findAllConNumeroPositivo();
    List<CuentaSearch> findByStrings(List<String> conditions, Boolean visible);
    Optional<Cuenta> update(BigDecimal numeroCuenta, Cuenta cuenta);
    void deleteById(BigDecimal numeroCuenta);
    List<Cuenta> saveAll(List<Cuenta> cuentas);
}
