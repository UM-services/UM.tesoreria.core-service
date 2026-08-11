package um.tesoreria.core.hexagonal.contratos.contrato.domain.ports.in;
import um.tesoreria.core.hexagonal.contratos.contrato.domain.model.Contrato;
import java.time.OffsetDateTime;
import java.util.List;
public interface GetAllContratosAjustablesUseCase {
    List<Contrato> getContratosAjustables(OffsetDateTime referencia);
}
