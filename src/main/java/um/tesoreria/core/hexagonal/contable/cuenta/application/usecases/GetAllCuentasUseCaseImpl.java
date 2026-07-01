package um.tesoreria.core.hexagonal.contable.cuenta.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.GetAllCuentasUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaRepository;
import java.util.List;
@Component
@RequiredArgsConstructor
public class GetAllCuentasUseCaseImpl implements GetAllCuentasUseCase {
    private final CuentaRepository repository;
    @Override
    public List<Cuenta> getAllCuentas() {
        return repository.findAll();
    }
}
