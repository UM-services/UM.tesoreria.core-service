package um.tesoreria.core.hexagonal.contrato.application.usecases;
import um.tesoreria.core.hexagonal.contrato.domain.model.Contrato;
import um.tesoreria.core.hexagonal.contrato.domain.ports.in.GetAllContratosByPersonaUseCase;
import um.tesoreria.core.hexagonal.contrato.domain.ports.out.ContratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllContratosByPersonaUseCaseImpl implements GetAllContratosByPersonaUseCase {
    private final ContratoRepository contratoRepository;
    @Override
    public List<Contrato> getContratosByPersona(BigDecimal personaId, Integer documentoId) {
        return contratoRepository.findAllByPersona(personaId, documentoId);
    }
}
