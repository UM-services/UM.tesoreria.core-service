package um.tesoreria.core.hexagonal.contable.cuenta.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.contable.cuenta.application.exception.CuentaConflictoException;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.CuentaJerarquia;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaHistorialPort;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.DeleteCuentaUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaRepository;
import java.math.BigDecimal;
@Component
@RequiredArgsConstructor
public class DeleteCuentaUseCaseImpl implements DeleteCuentaUseCase {
    private final CuentaRepository repository;
    private final CuentaHistorialPort historial;
    private final TransactionOperations transaction;
    @Override
    public void deleteCuenta(BigDecimal numeroCuenta) {
        transaction.executeWithoutResult(status -> repository.findWithLockByNumeroCuenta(numeroCuenta).ifPresent(anterior -> {
            final CuentaJerarquia.Rango rango;
            try {
                rango = CuentaJerarquia.rangoDescendientes(numeroCuenta);
            } catch (IllegalArgumentException ex) {
                throw new CuentaConflictoException(ex.getMessage());
            }
            if (repository.existenHijas(rango.desde(), rango.hasta())) {
                throw new CuentaConflictoException("La cuenta " + numeroCuenta + " tiene cuentas hijas");
            }
            if (repository.existenMovimientos(rango.desde(), rango.hasta())) {
                throw new CuentaConflictoException("La cuenta " + numeroCuenta + " contiene registraciones");
            }
            // La FK RESTRICT cierra la carrera con escritores de movcon externos a Java.
            historial.registrarBaja(anterior);
            repository.deleteById(numeroCuenta);
        }));
    }
}
