package um.tesoreria.core.hexagonal.cuenta.domain.ports.out;

import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.cuenta.domain.model.CuentaSearch;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CuentaRepository {
    Cuenta create(Cuenta cuenta);
    Optional<Cuenta> findById(BigDecimal numeroCuenta);
    Optional<Cuenta> findByCuentaContableId(Long cuentaContableId);
    List<Cuenta> findAll();
    List<Cuenta> findAllByNumeroCuentaIn(List<BigDecimal> cuentas);
    List<Cuenta> findAllGrado5();
    List<Cuenta> findAllByCierreResultado();
    List<Cuenta> findAllByCierreActivoPasivo();
    List<CuentaSearch> findByStrings(List<String> conditions, Boolean visible);
    Optional<Cuenta> update(BigDecimal numeroCuenta, Cuenta cuenta);
    void deleteById(BigDecimal numeroCuenta);
    List<Cuenta> saveAll(List<Cuenta> cuentas);
}
