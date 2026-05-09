package um.tesoreria.core.hexagonal.facultad.infrastructure.persistence.adapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.facultad.domain.ports.out.FacultadRepository;
import um.tesoreria.core.hexagonal.facultad.infrastructure.persistence.mapper.FacultadMapper;
import um.tesoreria.core.hexagonal.facultad.infrastructure.persistence.repository.JpaFacultadRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaFacultadRepositoryAdapter implements FacultadRepository {
    private final JpaFacultadRepository repository;
    private final FacultadMapper mapper;

    @Override
    public List<Facultad> findAll() {
        return repository.findAll(Sort.by("facultadId").ascending()).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Facultad> findAllIn(List<Integer> ids) {
        return repository.findAllByFacultadIdIn(ids).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Facultad> findById(Integer facultadId) {
        return repository.findByFacultadId(facultadId).map(mapper::toDomain);
    }
}
