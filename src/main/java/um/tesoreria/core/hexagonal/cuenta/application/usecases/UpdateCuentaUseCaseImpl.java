package um.tesoreria.core.hexagonal.cuenta.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.cuenta.domain.ports.in.UpdateCuentaUseCase;
import um.tesoreria.core.hexagonal.cuenta.domain.ports.out.CuentaRepository;
import java.math.BigDecimal;
import java.util.Optional;
@Component
@RequiredArgsConstructor
public class UpdateCuentaUseCaseImpl implements UpdateCuentaUseCase {
    private final CuentaRepository repository;
    @Override
    public Optional<Cuenta> updateCuenta(BigDecimal numeroCuenta, Cuenta cuenta) {
        return repository.update(numeroCuenta, cuenta);
    }
}
