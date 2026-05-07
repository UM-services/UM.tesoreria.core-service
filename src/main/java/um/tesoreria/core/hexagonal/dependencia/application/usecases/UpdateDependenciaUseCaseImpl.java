package um.tesoreria.core.hexagonal.dependencia.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.exception.DependenciaException;
import um.tesoreria.core.hexagonal.dependencia.domain.model.Dependencia;
import um.tesoreria.core.hexagonal.dependencia.domain.ports.in.UpdateDependenciaUseCase;
import um.tesoreria.core.hexagonal.dependencia.domain.ports.out.DependenciaRepository;
@Component
@RequiredArgsConstructor
public class UpdateDependenciaUseCaseImpl implements UpdateDependenciaUseCase {
    private final DependenciaRepository repository;
    @Override
    public Dependencia update(Integer id, Dependencia data) {
        Dependencia existing = repository.findById(id).orElseThrow(() -> new DependenciaException(id));
        existing.setNombre(data.getNombre());
        existing.setAcronimo(data.getAcronimo());
        existing.setFacultadId(data.getFacultadId());
        existing.setGeograficaId(data.getGeograficaId());
        existing.setCuentaHonorariosPagar(data.getCuentaHonorariosPagar());
        return repository.save(existing);
    }
}
