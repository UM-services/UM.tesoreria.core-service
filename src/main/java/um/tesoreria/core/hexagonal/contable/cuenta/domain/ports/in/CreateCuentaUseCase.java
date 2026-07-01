package um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
public interface CreateCuentaUseCase {
    Cuenta createCuenta(Cuenta cuenta);
}
