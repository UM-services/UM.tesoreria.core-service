package um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.articulo.domain.ports.out.ArticuloRepository;
import um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.entity.ArticuloEntity;
import um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.mapper.ArticuloMapper;
import um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.repository.JpaArticuloRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaArticuloRepositoryAdapter implements ArticuloRepository {

    private final JpaArticuloRepository jpaArticuloRepository;
    private final ArticuloMapper articuloMapper;

    @Override
    public Articulo create(Articulo articulo) {
        ArticuloEntity entity = articuloMapper.toEntity(articulo);
        ArticuloEntity saved = jpaArticuloRepository.save(entity);
        return articuloMapper.toDomainModel(saved);
    }

    @Override
    public Optional<Articulo> findById(Long id) {
        return jpaArticuloRepository.findById(id).map(articuloMapper::toDomainModel);
    }

    @Override
    public List<Articulo> findAll() {
        return jpaArticuloRepository.findAll().stream()
                .map(articuloMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Articulo> update(Long id, Articulo articulo) {
        if (jpaArticuloRepository.existsById(id)) {
            ArticuloEntity entity = articuloMapper.toEntity(articulo);
            entity.setArticuloId(id);
            ArticuloEntity updated = jpaArticuloRepository.save(entity);
            return Optional.of(articuloMapper.toDomainModel(updated));
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteById(Long id) {
        if (jpaArticuloRepository.existsById(id)) {
            jpaArticuloRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Articulo> findLast() {
        return jpaArticuloRepository.findTopByOrderByArticuloIdDesc().map(articuloMapper::toDomainModel);
    }
}
