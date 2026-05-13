package um.tesoreria.core.hexagonal.contrato.application.usecases;
import um.tesoreria.core.hexagonal.contrato.domain.model.Contrato;
import um.tesoreria.core.hexagonal.contrato.domain.ports.in.UpdateContratoUseCase;
import um.tesoreria.core.hexagonal.contrato.domain.ports.out.ContratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateContratoUseCaseImpl implements UpdateContratoUseCase {
    private final ContratoRepository contratoRepository;
    @Override
    public Optional<Contrato> updateContrato(Long id, Contrato contrato) {
        return contratoRepository.update(id, contrato);
    }
}
