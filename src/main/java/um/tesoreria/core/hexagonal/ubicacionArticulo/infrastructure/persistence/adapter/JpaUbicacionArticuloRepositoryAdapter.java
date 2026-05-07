package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.out.UbicacionArticuloRepository;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.entity.UbicacionArticuloEntity;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.mapper.UbicacionArticuloMapper;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.repository.JpaUbicacionArticuloRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaUbicacionArticuloRepositoryAdapter implements UbicacionArticuloRepository {
    private final JpaUbicacionArticuloRepository jpaUbicacionArticuloRepository;
    private final UbicacionArticuloMapper mapper;

    @Override
    public UbicacionArticulo save(UbicacionArticulo domain) {
        UbicacionArticuloEntity entity = mapper.toEntity(domain);
        // Check if updating existing record
        jpaUbicacionArticuloRepository.findByUbicacionIdAndArticuloId(domain.getUbicacionId(), domain.getArticuloId())
                .ifPresent(existing -> entity.setUbicacionArticuloId(existing.getUbicacionArticuloId()));

        return mapper.toDomainModel(jpaUbicacionArticuloRepository.save(entity));
    }
    
    @Override
    public List<UbicacionArticulo> findAllByArticuloId(Long articuloId) {
        return jpaUbicacionArticuloRepository.findAllByArticuloId(articuloId).stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<UbicacionArticulo> findAll() {
        return jpaUbicacionArticuloRepository.findAll().stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<UbicacionArticulo> findByUbicacionIdAndArticuloId(Integer ubicacionId, Long articuloId) {
        return jpaUbicacionArticuloRepository.findByUbicacionIdAndArticuloId(ubicacionId, articuloId)
                .map(mapper::toDomainModel);
    }
    
}