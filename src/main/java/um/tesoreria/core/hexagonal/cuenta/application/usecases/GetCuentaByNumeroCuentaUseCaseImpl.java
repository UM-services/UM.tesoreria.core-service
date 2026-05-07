package um.tesoreria.core.hexagonal.cuenta.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.cuenta.domain.ports.in.GetCuentaByNumeroCuentaUseCase;
import um.tesoreria.core.hexagonal.cuenta.domain.ports.out.CuentaRepository;
import java.math.BigDecimal;
import java.util.Optional;
@Component
@RequiredArgsConstructor
public class GetCuentaByNumeroCuentaUseCaseImpl implements GetCuentaByNumeroCuentaUseCase {
    private final CuentaRepository repository;
    @Override
    public Optional<Cuenta> getCuentaByNumeroCuenta(BigDecimal numeroCuenta) {
        return repository.findById(numeroCuenta);
    }
}
