package um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.CuentaSearch;
import java.util.List;
public interface SearchCuentasUseCase {
    List<CuentaSearch> searchCuentas(List<String> conditions, Boolean visible);
}
