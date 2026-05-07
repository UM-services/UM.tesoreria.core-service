package um.tesoreria.core.hexagonal.cuenta.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.cuenta.domain.model.CuentaSearch;
import um.tesoreria.core.hexagonal.cuenta.domain.ports.in.*;
import um.tesoreria.core.hexagonal.cuenta.domain.ports.in.SaveAllCuentasUseCase;
import um.tesoreria.core.hexagonal.cuenta.domain.ports.out.CuentaRepository;

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
    private final SaveAllCuentasUseCase saveAllCuentasUseCase;

    private final CuentaRepository repositoryPort;

    public List<Cuenta> findAll() {
        return getAllCuentasUseCase.getAllCuentas();
    }

    public List<Cuenta> findAllByNumeroCuentaIn(List<BigDecimal> cuentas) {
        return repositoryPort.findAllByNumeroCuentaIn(cuentas);
    }

    public List<Cuenta> findAllGrado5() {
        return repositoryPort.findAllGrado5();
    }

    public List<Cuenta> findAllByCierreResultado() {
        return repositoryPort.findAllByCierreResultado();
    }

    public List<Cuenta> findAllByCierreActivoPasivo() {
        return repositoryPort.findAllByCierreActivoPasivo();
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

    public List<Cuenta> saveAll(List<Cuenta> cuentas) {
        return saveAllCuentasUseCase.saveAllCuentas(cuentas);
    }

}
