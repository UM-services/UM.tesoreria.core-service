package um.tesoreria.core.hexagonal.dependencias.dependencia.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.dependencias.dependencia.domain.model.Dependencia;
import um.tesoreria.core.hexagonal.dependencias.dependencia.domain.ports.in.GetDependenciaByIdUseCase;
import um.tesoreria.core.hexagonal.dependencias.dependencia.domain.ports.out.DependenciaRepository;
import java.util.Optional;
@Component
@RequiredArgsConstructor
public class GetDependenciaByIdUseCaseImpl implements GetDependenciaByIdUseCase {
    private final DependenciaRepository repository;
    @Override public Optional<Dependencia> getById(Integer id) { return repository.findById(id); }
}
