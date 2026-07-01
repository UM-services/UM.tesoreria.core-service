package um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Sort;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.model.ArancelTipo;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.out.ArancelTipoRepository;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.entity.ArancelTipoEntity;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.mapper.ArancelTipoMapper;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.repository.JpaArancelTipoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaArancelTipoRepositoryAdapter implements ArancelTipoRepository {

    private final JpaArancelTipoRepository jpaArancelTipoRepository;
    private final ArancelTipoMapper arancelTipoMapper;

    @Override
    public List<ArancelTipo> findAll() {
        return jpaArancelTipoRepository.findAll(Sort.by("arancelTipoId").ascending()).stream()
                .map(arancelTipoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArancelTipo> findAllByHabilitado(Byte habilitado) {
        return jpaArancelTipoRepository.findAllByHabilitado(habilitado, Sort.by("arancelTipoId").ascending()).stream()
                .map(arancelTipoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ArancelTipo> findByArancelTipoId(Integer id) {
        return jpaArancelTipoRepository.findByArancelTipoId(id)
                .map(arancelTipoMapper::toDomainModel);
    }

    @Override
    public Optional<ArancelTipo> findByArancelTipoIdCompleto(Integer id) {
        return jpaArancelTipoRepository.findByArancelTipoIdCompleto(id)
                .map(arancelTipoMapper::toDomainModel);
    }

    @Override
    public Optional<ArancelTipo> findLast() {
        return jpaArancelTipoRepository.findTopByOrderByArancelTipoIdDesc()
                .map(arancelTipoMapper::toDomainModel);
    }

    @Override
    public ArancelTipo create(ArancelTipo arancelTipo) {
        ArancelTipoEntity entity = arancelTipoMapper.toEntity(arancelTipo);
        ArancelTipoEntity saved = jpaArancelTipoRepository.save(entity);
        return arancelTipoMapper.toDomainModel(saved);
    }

    @Override
    public Optional<ArancelTipo> update(Integer id, ArancelTipo arancelTipo) {
        return jpaArancelTipoRepository.findByArancelTipoId(id).map(entity -> {
            entity.setDescripcion(arancelTipo.getDescripcion());
            entity.setMedioArancel(arancelTipo.getMedioArancel());
            entity.setArancelTipoIdCompleto(arancelTipo.getArancelTipoIdCompleto());
            entity.setHabilitado(arancelTipo.getHabilitado());
            ArancelTipoEntity updated = jpaArancelTipoRepository.save(entity);
            return arancelTipoMapper.toDomainModel(updated);
        });
    }

    @Override
    public void deleteById(Integer id) {
        jpaArancelTipoRepository.deleteByArancelTipoId(id);
    }
}
