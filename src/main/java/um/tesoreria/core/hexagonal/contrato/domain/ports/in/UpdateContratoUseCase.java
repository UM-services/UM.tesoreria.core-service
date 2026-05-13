package um.tesoreria.core.hexagonal.contrato.domain.ports.in;
import um.tesoreria.core.hexagonal.contrato.domain.model.Contrato;
import java.util.Optional;
public interface UpdateContratoUseCase {
    Optional<Contrato> updateContrato(Long id, Contrato contrato);
}
