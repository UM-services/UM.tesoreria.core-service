package um.tesoreria.core.hexagonal.contrato.domain.ports.in;
import um.tesoreria.core.hexagonal.contrato.domain.model.Contrato;
public interface CreateContratoUseCase {
    Contrato createContrato(Contrato contrato);
}
