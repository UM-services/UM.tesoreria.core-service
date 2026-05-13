package um.tesoreria.core.hexagonal.contrato.application.usecases;
import um.tesoreria.core.hexagonal.contrato.domain.model.Contrato;
import um.tesoreria.core.hexagonal.contrato.domain.ports.in.SaveAllContratosUseCase;
import um.tesoreria.core.hexagonal.contrato.domain.ports.out.ContratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SaveAllContratosUseCaseImpl implements SaveAllContratosUseCase {
    private final ContratoRepository contratoRepository;
    @Override
    public List<Contrato> saveAllContratos(List<Contrato> contratos) {
        return contratoRepository.saveAll(contratos);
    }
}
