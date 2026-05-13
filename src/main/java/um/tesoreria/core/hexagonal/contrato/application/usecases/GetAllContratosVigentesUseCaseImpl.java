package um.tesoreria.core.hexagonal.contrato.application.usecases;
import um.tesoreria.core.hexagonal.contrato.domain.model.Contrato;
import um.tesoreria.core.hexagonal.contrato.domain.ports.in.GetAllContratosVigentesUseCase;
import um.tesoreria.core.hexagonal.contrato.domain.ports.out.ContratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllContratosVigentesUseCaseImpl implements GetAllContratosVigentesUseCase {
    private final ContratoRepository contratoRepository;
    @Override
    public List<Contrato> getContratosVigentes(OffsetDateTime referencia) {
        return contratoRepository.findAllVigente(referencia);
    }
}
