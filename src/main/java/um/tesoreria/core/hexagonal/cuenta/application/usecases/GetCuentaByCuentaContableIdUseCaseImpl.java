package um.tesoreria.core.hexagonal.cuenta.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.cuenta.domain.ports.in.GetCuentaByCuentaContableIdUseCase;
import um.tesoreria.core.hexagonal.cuenta.domain.ports.out.CuentaRepository;
import java.util.Optional;
@Component
@RequiredArgsConstructor
public class GetCuentaByCuentaContableIdUseCaseImpl implements GetCuentaByCuentaContableIdUseCase {
    private final CuentaRepository repository;
    @Override
    public Optional<Cuenta> getCuentaByCuentaContableId(Long cuentaContableId) {
        return repository.findByCuentaContableId(cuentaContableId);
    }
}
