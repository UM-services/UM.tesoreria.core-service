package um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.application.usecases;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.model.GuaraniPropuestaTipoChequera;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.ports.in.GetGuaraniPropuestaTipoChequeraByPropuestaAndLectivoUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.ports.out.GuaraniPropuestaTipoChequeraRepository;

@Component
@RequiredArgsConstructor
public class GetGuaraniPropuestaTipoChequeraByPropuestaAndLectivoUseCaseImpl
        implements GetGuaraniPropuestaTipoChequeraByPropuestaAndLectivoUseCase {
    private final GuaraniPropuestaTipoChequeraRepository repository;

    @Override
    public Optional<GuaraniPropuestaTipoChequera> getByPropuestaAndLectivo(
            Integer propuestaGuarani, Integer lectivoId) {
        return repository.findByPropuestaGuaraniAndLectivoId(propuestaGuarani, lectivoId);
    }
}
