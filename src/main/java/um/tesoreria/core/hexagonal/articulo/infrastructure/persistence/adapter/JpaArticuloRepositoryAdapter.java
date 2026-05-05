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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import um.tesoreria.core.hexagonal.articulo.domain.model.ArticuloSearch;
import um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.repository.ArticuloKeyRepository;
import um.tesoreria.core.model.PaginatedResponse;


@Component
@RequiredArgsConstructor
public class JpaArticuloRepositoryAdapter implements ArticuloRepository {

    private final JpaArticuloRepository jpaArticuloRepository;
    private final ArticuloMapper articuloMapper;
    private final ArticuloKeyRepository articuloKeyRepository;

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
    public PaginatedResponse<Articulo> findAllPaginatedByTipo(String tipo, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("nombre").ascending());
        Page<ArticuloEntity> entityPage = jpaArticuloRepository.findAllByTipo(tipo, pageRequest);
        List<Articulo> domainList = entityPage.getContent().stream()
                .map(articuloMapper::toDomainModel)
                .collect(Collectors.toList());
        return new PaginatedResponse<>(domainList, entityPage.getTotalElements(), entityPage.getTotalPages(), entityPage.getNumber(), entityPage.getSize());
    }

    @Override
    public List<ArticuloSearch> findAllByStrings(List<String> conditions) {
        return articuloKeyRepository.findAllByStrings(conditions).stream()
                .map(articuloMapper::toSearchDomain)
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
