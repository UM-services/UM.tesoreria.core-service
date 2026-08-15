package um.tesoreria.core.hexagonal.dependencias.dependencia.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.dependencias.dependencia.domain.model.Dependencia;
import um.tesoreria.core.hexagonal.dependencias.dependencia.domain.ports.in.GetAllDependenciasUseCase;
import um.tesoreria.core.hexagonal.dependencias.dependencia.domain.ports.out.DependenciaRepository;
import java.util.List;
@Component
@RequiredArgsConstructor
public class GetAllDependenciasUseCaseImpl implements GetAllDependenciasUseCase {
    private final DependenciaRepository repository;
    @Override public List<Dependencia> getAll() { return repository.findAll(); }
}
