package um.tesoreria.core.hexagonal.contratos.contrato.domain.ports.in;
import um.tesoreria.core.hexagonal.contratos.contrato.domain.model.Contrato;
import java.util.List;
public interface SaveAllContratosUseCase {
    List<Contrato> saveAllContratos(List<Contrato> contratos);
}
