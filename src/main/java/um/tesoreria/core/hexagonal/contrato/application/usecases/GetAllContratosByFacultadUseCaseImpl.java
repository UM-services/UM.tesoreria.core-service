package um.tesoreria.core.hexagonal.contrato.application.usecases;
import um.tesoreria.core.hexagonal.contrato.domain.model.Contrato;
import um.tesoreria.core.hexagonal.contrato.domain.ports.in.GetAllContratosByFacultadUseCase;
import um.tesoreria.core.hexagonal.contrato.domain.ports.out.ContratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllContratosByFacultadUseCaseImpl implements GetAllContratosByFacultadUseCase {
    private final ContratoRepository contratoRepository;
    @Override
    public List<Contrato> getContratosByFacultad(Integer facultadId, Integer geograficaId) {
        return contratoRepository.findAllByFacultad(facultadId, geograficaId);
    }
}
