package um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.model.GuaraniPropuestaTipoChequera;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.ports.in.UpdateGuaraniPropuestaTipoChequeraUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.ports.out.GuaraniPropuestaTipoChequeraRepository;

@Component
@RequiredArgsConstructor
public class UpdateGuaraniPropuestaTipoChequeraUseCaseImpl implements UpdateGuaraniPropuestaTipoChequeraUseCase {
    private final GuaraniPropuestaTipoChequeraRepository repository;

    @Override
    public GuaraniPropuestaTipoChequera update(GuaraniPropuestaTipoChequera data) {
        return repository.save(data);
    }
}
