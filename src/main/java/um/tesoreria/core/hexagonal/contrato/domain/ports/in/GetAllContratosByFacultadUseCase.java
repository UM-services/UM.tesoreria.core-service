package um.tesoreria.core.hexagonal.contrato.domain.ports.in;
import um.tesoreria.core.hexagonal.contrato.domain.model.Contrato;
import java.util.List;
public interface GetAllContratosByFacultadUseCase {
    List<Contrato> getContratosByFacultad(Integer facultadId, Integer geograficaId);
}
