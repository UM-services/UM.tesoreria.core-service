package um.tesoreria.core.hexagonal.contable.cuenta.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.RecalculaGradosUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaRepository;

@Component
@RequiredArgsConstructor
public class RecalculaGradosUseCaseImpl implements RecalculaGradosUseCase {
    private final CuentaRepository repository;

    @Override
    public String recalculaGrados() {
        repository.recalcularGrados();
        return "Ok";
    }
}
