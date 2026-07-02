package um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import java.util.Optional;
public interface GetCuentaByCuentaContableIdUseCase {
    Optional<Cuenta> getCuentaByCuentaContableId(Long cuentaContableId);
}
