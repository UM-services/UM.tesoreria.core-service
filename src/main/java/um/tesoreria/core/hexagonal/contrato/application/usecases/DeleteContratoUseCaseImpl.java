package um.tesoreria.core.hexagonal.contrato.application.usecases;
import um.tesoreria.core.hexagonal.contrato.domain.ports.in.DeleteContratoUseCase;
import um.tesoreria.core.hexagonal.contrato.domain.ports.out.ContratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteContratoUseCaseImpl implements DeleteContratoUseCase {
    private final ContratoRepository contratoRepository;
    @Override
    public boolean deleteContrato(Long id) {
        return contratoRepository.deleteById(id);
    }
}
