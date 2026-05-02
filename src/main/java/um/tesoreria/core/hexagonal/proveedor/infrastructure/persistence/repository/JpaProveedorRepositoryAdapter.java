package um.tesoreria.core.hexagonal.proveedor.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.proveedor.domain.model.ProveedorSearch;
import um.tesoreria.core.hexagonal.proveedor.domain.ports.out.ProveedorRepository;
import um.tesoreria.core.hexagonal.proveedor.infrastructure.persistence.entity.ProveedorEntity;
import um.tesoreria.core.hexagonal.proveedor.infrastructure.persistence.mapper.ProveedorMapper;
import um.tesoreria.core.service.view.ProveedorSearchService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaProveedorRepositoryAdapter implements ProveedorRepository {

    private final JpaProveedorRepository jpaProveedorRepository;
    private final ProveedorSearchService proveedorSearchService;
    private final ProveedorMapper proveedorMapper;

    @Override
    public Proveedor create(Proveedor proveedor) {
        ProveedorEntity entity = proveedorMapper.toEntity(proveedor);
        ProveedorEntity savedEntity = jpaProveedorRepository.save(entity);
        return proveedorMapper.toDomainModel(savedEntity);
    }

    @Override
    public Optional<Proveedor> findByProveedorId(Integer proveedorId) {
        return jpaProveedorRepository.findByProveedorId(proveedorId)
                .map(proveedorMapper::toDomainModel);
    }

    @Override
    public Optional<Proveedor> findByCuit(String cuit) {
        return jpaProveedorRepository.findTopByCuit(cuit)
                .map(proveedorMapper::toDomainModel);
    }

    @Override
    public Optional<Proveedor> findLast() {
        return jpaProveedorRepository.findTopByOrderByProveedorIdDesc()
                .map(proveedorMapper::toDomainModel);
    }

    @Override
    public List<Proveedor> findAll() {
        return jpaProveedorRepository.findAll().stream()
                .map(proveedorMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProveedorSearch> findAllByStrings(List<String> conditions) {
        return proveedorSearchService.findAllByStrings(conditions).stream()
                .map(proveedorMapper::toSearchDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Proveedor> update(Integer proveedorId, Proveedor proveedor) {
        return jpaProveedorRepository.findByProveedorId(proveedorId).map(existingEntity -> {
            ProveedorEntity entityToSave = proveedorMapper.toEntity(proveedor);
            entityToSave.setProveedorId(proveedorId);
            ProveedorEntity updatedEntity = jpaProveedorRepository.save(entityToSave);
            return proveedorMapper.toDomainModel(updatedEntity);
        });
    }

    @Override
    public boolean deleteById(Integer proveedorId) {
        if (jpaProveedorRepository.existsById(proveedorId)) {
            jpaProveedorRepository.deleteById(proveedorId);
            return true;
        }
        return false;
    }
}
