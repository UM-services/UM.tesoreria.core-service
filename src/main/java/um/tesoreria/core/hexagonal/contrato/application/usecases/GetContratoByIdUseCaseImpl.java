package um.tesoreria.core.hexagonal.contrato.application.usecases;
import um.tesoreria.core.hexagonal.contrato.domain.model.Contrato;
import um.tesoreria.core.hexagonal.contrato.domain.ports.in.GetContratoByIdUseCase;
import um.tesoreria.core.hexagonal.contrato.domain.ports.out.ContratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetContratoByIdUseCaseImpl implements GetContratoByIdUseCase {
    private final ContratoRepository contratoRepository;
    @Override
    public Optional<Contrato> getContratoById(Long id) {
        return contratoRepository.findById(id);
    }
}
