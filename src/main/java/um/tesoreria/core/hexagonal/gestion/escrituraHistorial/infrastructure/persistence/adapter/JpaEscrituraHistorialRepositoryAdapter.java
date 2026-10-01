package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.model.EscrituraHistorial;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.out.EscrituraHistorialRepository;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.mapper.EscrituraHistorialMapper;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.repository.JpaEscrituraHistorialRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JpaEscrituraHistorialRepositoryAdapter implements EscrituraHistorialRepository {

    private final JpaEscrituraHistorialRepository jpaEscrituraHistorialRepository;
    private final EscrituraHistorialMapper escrituraHistorialMapper;

    @Override
    public EscrituraHistorial save(EscrituraHistorial historial) {
        var entity = escrituraHistorialMapper.toEntity(historial);
        var saved = jpaEscrituraHistorialRepository.save(entity);
        return escrituraHistorialMapper.toDomain(saved);
    }

    @Override
    public List<EscrituraHistorial> findAllByEntidadAndEntidadClaveOrderByFechaAscEscrituraHistorialIdAsc(
            String entidad, String entidadClave) {
        return jpaEscrituraHistorialRepository
                .findAllByEntidadAndEntidadClaveOrderByFechaAscEscrituraHistorialIdAsc(entidad, entidadClave)
                .stream()
                .map(escrituraHistorialMapper::toDomain)
                .toList();
    }
}
