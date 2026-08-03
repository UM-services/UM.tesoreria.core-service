package um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.ports.in.DeleteGuaraniPropuestaTipoChequeraUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.ports.out.GuaraniPropuestaTipoChequeraRepository;

@Component
@RequiredArgsConstructor
public class DeleteGuaraniPropuestaTipoChequeraUseCaseImpl implements DeleteGuaraniPropuestaTipoChequeraUseCase {
    private final GuaraniPropuestaTipoChequeraRepository repository;

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
