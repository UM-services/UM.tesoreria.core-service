package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.model.GuaraniUbicacion;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.out.GuaraniUbicacionRepository;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.infrastructure.persistence.mapper.GuaraniUbicacionMapper;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.infrastructure.persistence.repository.JpaGuaraniUbicacionRepository;

@Component
@RequiredArgsConstructor
public class JpaGuaraniUbicacionRepositoryAdapter implements GuaraniUbicacionRepository {
    private final JpaGuaraniUbicacionRepository jpaGuaraniUbicacionRepository;
    private final GuaraniUbicacionMapper guaraniUbicacionMapper;

    @Override
    public List<GuaraniUbicacion> findAll() {
        return jpaGuaraniUbicacionRepository.findAll().stream()
                .map(guaraniUbicacionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<GuaraniUbicacion> findById(Integer id) {
        return jpaGuaraniUbicacionRepository.findById(id)
                .map(guaraniUbicacionMapper::toDomain);
    }

    @Override
    public Optional<GuaraniUbicacion> findByUbicacion(Integer ubicacion) {
        return jpaGuaraniUbicacionRepository.findByUbicacion(ubicacion)
                .map(guaraniUbicacionMapper::toDomain);
    }

    @Override
    public GuaraniUbicacion save(GuaraniUbicacion guaraniUbicacion) {
        return guaraniUbicacionMapper.toDomain(
                jpaGuaraniUbicacionRepository.save(guaraniUbicacionMapper.toEntity(guaraniUbicacion)));
    }

    @Override
    public void deleteById(Integer id) {
        jpaGuaraniUbicacionRepository.deleteById(id);
    }
}
