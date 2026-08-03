package um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.model.GuaraniPropuestaTipoChequera;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.ports.in.CreateGuaraniPropuestaTipoChequeraUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.ports.out.GuaraniPropuestaTipoChequeraRepository;

@Component
@RequiredArgsConstructor
public class CreateGuaraniPropuestaTipoChequeraUseCaseImpl implements CreateGuaraniPropuestaTipoChequeraUseCase {
    private final GuaraniPropuestaTipoChequeraRepository repository;

    @Override
    public GuaraniPropuestaTipoChequera create(GuaraniPropuestaTipoChequera data) {
        return repository.save(data);
    }
}
