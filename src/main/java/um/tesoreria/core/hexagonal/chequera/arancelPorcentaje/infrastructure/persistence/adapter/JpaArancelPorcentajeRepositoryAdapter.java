package um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.model.ArancelPorcentaje;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.ports.out.ArancelPorcentajeRepository;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.infrastructure.persistence.mapper.ArancelPorcentajeMapper;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.infrastructure.persistence.repository.JpaArancelPorcentajeRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaArancelPorcentajeRepositoryAdapter implements ArancelPorcentajeRepository {

    private final JpaArancelPorcentajeRepository jpaArancelPorcentajeRepository;
    private final ArancelPorcentajeMapper arancelPorcentajeMapper;

    @Override
    public List<ArancelPorcentaje> findAllByAranceltipoId(Integer aranceltipoId) {
        return jpaArancelPorcentajeRepository.findAllByAranceltipoId(aranceltipoId).stream()
                .map(arancelPorcentajeMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ArancelPorcentaje> findByUnique(Integer aranceltipoId, Integer productoId) {
        return jpaArancelPorcentajeRepository.findByAranceltipoIdAndProductoId(aranceltipoId, productoId)
                .map(arancelPorcentajeMapper::toDomainModel);
    }

    @Override
    public ArancelPorcentaje add(ArancelPorcentaje arancelPorcentaje) {
        var entity = arancelPorcentajeMapper.toEntity(arancelPorcentaje);
        var saved = jpaArancelPorcentajeRepository.save(entity);
        return arancelPorcentajeMapper.toDomainModel(saved);
    }

    @Override
    public Optional<ArancelPorcentaje> findById(Long arancelporcentajeId) {
        return jpaArancelPorcentajeRepository.findById(arancelporcentajeId)
                .map(arancelPorcentajeMapper::toDomainModel);
    }

    @Override
    public ArancelPorcentaje update(ArancelPorcentaje arancelPorcentaje) {
        var entity = arancelPorcentajeMapper.toEntity(arancelPorcentaje);
        var saved = jpaArancelPorcentajeRepository.save(entity);
        return arancelPorcentajeMapper.toDomainModel(saved);
    }
}
