package um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.model.Campanha;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.ports.out.CampanhaRepository;
import um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.persistence.entity.CampanhaEntity;
import um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.persistence.mapper.CampanhaMapper;
import um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.persistence.repository.JpaCampanhaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaCampanhaRepositoryAdapter implements CampanhaRepository {

    private final JpaCampanhaRepository jpaCampanhaRepository;
    private final CampanhaMapper campanhaMapper;

    @Override
    public Campanha create(Campanha campanha) {
        CampanhaEntity entity = campanhaMapper.toEntity(campanha);
        CampanhaEntity saved = jpaCampanhaRepository.save(entity);
        return campanhaMapper.toDomainModel(saved);
    }

    @Override
    public Optional<Campanha> findById(UUID id) {
        return jpaCampanhaRepository.findById(id)
                .map(campanhaMapper::toDomainModel);
    }

    @Override
    public List<Campanha> findAll() {
        return jpaCampanhaRepository.findAll().stream()
                .map(campanhaMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Campanha> update(UUID campanhaId, Campanha campanha) {
        if (jpaCampanhaRepository.existsById(campanhaId)) {
            CampanhaEntity entity = campanhaMapper.toEntity(campanha);
            entity.setCampanhaId(campanhaId);
            CampanhaEntity updated = jpaCampanhaRepository.save(entity);
            return Optional.of(campanhaMapper.toDomainModel(updated));
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteByCampanhaId(UUID campanhaId) {
        if (jpaCampanhaRepository.existsByCampanhaId(campanhaId)) {
            jpaCampanhaRepository.deleteByCampanhaId(campanhaId);
            return true;
        }
        return false;
    }
}
