package um.tesoreria.core.hexagonal.cuenta.domain.ports.in;
import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;
import java.math.BigDecimal;
import java.util.Optional;
public interface UpdateCuentaUseCase {
    Optional<Cuenta> updateCuenta(BigDecimal numeroCuenta, Cuenta cuenta);
}
