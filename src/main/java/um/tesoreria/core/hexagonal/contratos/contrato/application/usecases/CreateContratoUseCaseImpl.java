package um.tesoreria.core.hexagonal.contratos.contrato.application.usecases;
import um.tesoreria.core.hexagonal.contratos.contrato.domain.model.Contrato;
import um.tesoreria.core.hexagonal.contratos.contrato.domain.ports.in.CreateContratoUseCase;
import um.tesoreria.core.hexagonal.contratos.contrato.domain.ports.out.ContratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateContratoUseCaseImpl implements CreateContratoUseCase {
    private final ContratoRepository contratoRepository;
    @Override
    public Contrato createContrato(Contrato contrato) {
        return contratoRepository.create(contrato);
    }
}
