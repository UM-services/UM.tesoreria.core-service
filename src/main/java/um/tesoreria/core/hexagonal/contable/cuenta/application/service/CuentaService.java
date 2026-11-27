package um.tesoreria.core.hexagonal.contable.cuenta.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.CuentaSearch;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.RecalculoGrados;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.*;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CuentaService {

    private final CreateCuentaUseCase createCuentaUseCase;
    private final UpdateCuentaUseCase updateCuentaUseCase;
    private final DeleteCuentaUseCase deleteCuentaUseCase;
    private final GetAllCuentasUseCase getAllCuentasUseCase;
    private final GetCuentaByNumeroCuentaUseCase getCuentaByNumeroCuentaUseCase;
    private final GetCuentaByCuentaContableIdUseCase getCuentaByCuentaContableIdUseCase;
    private final SearchCuentasUseCase searchCuentasUseCase;
    private final RecalculaGradosUseCase recalculaGradosUseCase;
    private final RecalcularGradosYNombresUseCase recalcularGradosYNombresUseCase;
    private final GetCuentasImputablesUseCase getCuentasImputablesUseCase;
    private final SaveAllCuentasUseCase saveAllCuentasUseCase;

    private final CuentaRepository repositoryPort;

    public List<Cuenta> findAll() {
        return getAllCuentasUseCase.getAllCuentas();
    }

    public List<Cuenta> findAllByGeograficaIdAndGrado(Integer geograficaId, Integer grado) {
        return repositoryPort.findAllByGeograficaIdAndGrado(geograficaId, grado);
    }

    public List<Cuenta> findAllByNumeroCuentaIn(List<BigDecimal> cuentas) {
        return repositoryPort.findAllByNumeroCuentaIn(cuentas);
    }

    public List<Cuenta> findAllImputables() {
        return getCuentasImputablesUseCase.getCuentasImputables();
    }

    /** Cuentas imputables del grupo de resultados (30.000.000.000 a 49.999.999.999), en orden de número. */
    public List<Cuenta> findAllByCierreResultado() {
        return imputablesEntre(new BigDecimal(30000000000L), new BigDecimal(49999999999L));
    }

    /** Cuentas imputables de activo y pasivo (10.000.000.000 a 29.999.999.999), en orden de número. */
    public List<Cuenta> findAllByCierreActivoPasivo() {
        return imputablesEntre(new BigDecimal(10000000000L), new BigDecimal(29999999999L));
    }

    // El cierre y el balance tienen que ver el mismo grado 5: sale del número, no de la columna guardada.
    private List<Cuenta> imputablesEntre(BigDecimal desde, BigDecimal hasta) {
        return getCuentasImputablesUseCase.getCuentasImputables().stream()
                .filter(cuenta -> cuenta.getNumeroCuenta().compareTo(desde) >= 0
                        && cuenta.getNumeroCuenta().compareTo(hasta) <= 0)
                .toList();
    }

    public List<CuentaSearch> findByStrings(List<String> conditions, Boolean visible) {
        return searchCuentasUseCase.searchCuentas(conditions, visible);
    }

    public Optional<Cuenta> findByNumeroCuenta(BigDecimal numeroCuenta) {
        return getCuentaByNumeroCuentaUseCase.getCuentaByNumeroCuenta(numeroCuenta);
    }

    public Optional<Cuenta> findByCuentaContableId(Long cuentaContableId) {
        return getCuentaByCuentaContableIdUseCase.getCuentaByCuentaContableId(cuentaContableId);
    }

    public Cuenta add(Cuenta cuenta) {
        return createCuentaUseCase.createCuenta(cuenta);
    }

    public Optional<Cuenta> update(Cuenta cuenta, BigDecimal numeroCuenta) {
        return updateCuentaUseCase.updateCuenta(numeroCuenta, cuenta);
    }

    public void delete(BigDecimal numeroCuenta) {
        deleteCuentaUseCase.deleteCuenta(numeroCuenta);
    }

    public String recalculaGrados() {
        return recalculaGradosUseCase.recalculaGrados();
    }

    public RecalculoGrados recalcularGradosYNombres() {
        return recalcularGradosYNombresUseCase.recalcularGradosYNombres();
    }

    public List<Cuenta> saveAll(List<Cuenta> cuentas) {
        return saveAllCuentasUseCase.saveAllCuentas(cuentas);
    }

}
