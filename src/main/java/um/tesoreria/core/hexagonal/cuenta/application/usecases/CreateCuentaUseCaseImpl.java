package um.tesoreria.core.hexagonal.cuenta.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.cuenta.domain.ports.in.CreateCuentaUseCase;
import um.tesoreria.core.hexagonal.cuenta.domain.ports.out.CuentaRepository;
@Component
@RequiredArgsConstructor
public class CreateCuentaUseCaseImpl implements CreateCuentaUseCase {
    private final CuentaRepository repository;
    @Override
    public Cuenta createCuenta(Cuenta cuenta) {
        return repository.create(cuenta);
    }
}
