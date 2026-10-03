package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.model.EscrituraHistorial;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.out.EscrituraHistorialRepository;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.mapper.EscrituraHistorialMapper;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.repository.JpaEscrituraHistorialRepository;

@Component
@RequiredArgsConstructor
public class JpaEscrituraHistorialRepositoryAdapter implements EscrituraHistorialRepository {

    private final JpaEscrituraHistorialRepository jpaEscrituraHistorialRepository;
    private final EscrituraHistorialMapper escrituraHistorialMapper;

    @Override
    public EscrituraHistorial save(EscrituraHistorial historial) {
        // Con id, Spring Data haría merge y pisaría un evento ya registrado
        Assert.isNull(historial.getEscrituraHistorialId(), "un evento de historial no se modifica");
        var entity = escrituraHistorialMapper.toEntity(historial);
        var saved = jpaEscrituraHistorialRepository.save(entity);
        return escrituraHistorialMapper.toDomain(saved);
    }
}
