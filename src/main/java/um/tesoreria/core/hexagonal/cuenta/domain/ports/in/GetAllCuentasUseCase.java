package um.tesoreria.core.hexagonal.cuenta.domain.ports.in;
import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;
import java.util.List;
public interface GetAllCuentasUseCase {
    List<Cuenta> getAllCuentas();
}
