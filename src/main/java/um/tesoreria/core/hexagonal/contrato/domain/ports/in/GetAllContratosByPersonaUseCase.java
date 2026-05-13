package um.tesoreria.core.hexagonal.contrato.domain.ports.in;
import um.tesoreria.core.hexagonal.contrato.domain.model.Contrato;
import java.math.BigDecimal;
import java.util.List;
public interface GetAllContratosByPersonaUseCase {
    List<Contrato> getContratosByPersona(BigDecimal personaId, Integer documentoId);
}
