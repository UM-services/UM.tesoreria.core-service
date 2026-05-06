package um.tesoreria.core.hexagonal.ubicacion.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.ubicacion.domain.model.Ubicacion;
import um.tesoreria.core.hexagonal.ubicacion.domain.ports.out.UbicacionRepository;
import um.tesoreria.core.hexagonal.ubicacion.infrastructure.persistence.mapper.UbicacionMapper;
import um.tesoreria.core.hexagonal.ubicacion.infrastructure.persistence.repository.JpaUbicacionRepository;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaUbicacionRepositoryAdapter implements UbicacionRepository {
    private final JpaUbicacionRepository jpaUbicacionRepository;
    private final UbicacionMapper mapper;

    @Override
    public List<Ubicacion> findAll() {
        return jpaUbicacionRepository.findAll().stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ubicacion> findAllByGeograficaId(Integer geograficaId) {
        return jpaUbicacionRepository.findAllByDependenciaIdNotNull().stream()
                .filter(u -> u.getDependencia() != null && u.getDependencia().getGeograficaId().equals(geograficaId))
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }
}
