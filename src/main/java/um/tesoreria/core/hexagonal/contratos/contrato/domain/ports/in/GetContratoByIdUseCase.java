package um.tesoreria.core.hexagonal.contratos.contrato.domain.ports.in;
import um.tesoreria.core.hexagonal.contratos.contrato.domain.model.Contrato;
import java.util.Optional;
public interface GetContratoByIdUseCase {
    Optional<Contrato> getContratoById(Long id);
}
