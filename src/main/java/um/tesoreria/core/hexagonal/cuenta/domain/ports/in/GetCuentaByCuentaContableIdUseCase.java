package um.tesoreria.core.hexagonal.cuenta.domain.ports.in;
import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;
import java.util.Optional;
public interface GetCuentaByCuentaContableIdUseCase {
    Optional<Cuenta> getCuentaByCuentaContableId(Long cuentaContableId);
}
