package um.tesoreria.core.hexagonal.dependencia.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.dependencia.domain.model.Dependencia;
import um.tesoreria.core.hexagonal.dependencia.domain.ports.out.DependenciaRepository;
import um.tesoreria.core.hexagonal.dependencia.infrastructure.persistence.entity.DependenciaEntity;
import um.tesoreria.core.hexagonal.dependencia.infrastructure.persistence.mapper.DependenciaMapper;
import um.tesoreria.core.hexagonal.dependencia.infrastructure.persistence.repository.JpaDependenciaRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaDependenciaRepositoryAdapter implements DependenciaRepository {
    private final JpaDependenciaRepository repository;
    private final DependenciaMapper mapper;

    @Override
    public List<Dependencia> findAll() {
        return repository.findAllByOrderByNombre().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Dependencia> findById(Integer dependenciaId) {
        return repository.findByDependenciaId(dependenciaId).map(mapper::toDomain);
    }

    @Override
    public Dependencia save(Dependencia domain) {
        DependenciaEntity entity = mapper.toEntity(domain);
        return mapper.toDomain(repository.save(entity));
    }
}
