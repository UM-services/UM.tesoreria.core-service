package um.tesoreria.core.hexagonal.contratos.contrato.domain.ports.in;
import um.tesoreria.core.hexagonal.contratos.contrato.domain.model.Contrato;
public interface CreateContratoUseCase {
    Contrato createContrato(Contrato contrato);
}
