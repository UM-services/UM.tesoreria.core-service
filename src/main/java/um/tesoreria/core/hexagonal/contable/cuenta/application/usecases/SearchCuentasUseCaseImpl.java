package um.tesoreria.core.hexagonal.contable.cuenta.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.CuentaSearch;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.SearchCuentasUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaRepository;
import java.util.List;
@Component
@RequiredArgsConstructor
public class SearchCuentasUseCaseImpl implements SearchCuentasUseCase {
    private final CuentaRepository repository;
    @Override
    public List<CuentaSearch> searchCuentas(List<String> conditions, Boolean visible) {
        return repository.findByStrings(conditions, visible);
    }
}
