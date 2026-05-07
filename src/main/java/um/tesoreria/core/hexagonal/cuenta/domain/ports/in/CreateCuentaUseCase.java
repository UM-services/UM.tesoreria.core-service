package um.tesoreria.core.hexagonal.cuenta.domain.ports.in;
import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;
public interface CreateCuentaUseCase {
    Cuenta createCuenta(Cuenta cuenta);
}
