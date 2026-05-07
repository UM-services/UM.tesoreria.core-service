package um.tesoreria.core.hexagonal.cuenta.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cuenta.domain.ports.in.DeleteCuentaUseCase;
import um.tesoreria.core.hexagonal.cuenta.domain.ports.out.CuentaRepository;
import java.math.BigDecimal;
@Component
@RequiredArgsConstructor
public class DeleteCuentaUseCaseImpl implements DeleteCuentaUseCase {
    private final CuentaRepository repository;
    @Override
    public void deleteCuenta(BigDecimal numeroCuenta) {
        repository.deleteById(numeroCuenta);
    }
}
