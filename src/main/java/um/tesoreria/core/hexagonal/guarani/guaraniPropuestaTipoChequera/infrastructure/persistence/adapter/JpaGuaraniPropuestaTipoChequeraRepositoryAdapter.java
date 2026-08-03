package um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.infrastructure.persistence.adapter;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.model.GuaraniPropuestaTipoChequera;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.ports.out.GuaraniPropuestaTipoChequeraRepository;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.infrastructure.persistence.entity.GuaraniPropuestaTipoChequeraEntity;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.infrastructure.persistence.mapper.GuaraniPropuestaTipoChequeraMapper;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.infrastructure.persistence.repository.JpaGuaraniPropuestaTipoChequeraRepository;

@Component
@RequiredArgsConstructor
public class JpaGuaraniPropuestaTipoChequeraRepositoryAdapter implements GuaraniPropuestaTipoChequeraRepository {
    private final JpaGuaraniPropuestaTipoChequeraRepository repository;
    private final GuaraniPropuestaTipoChequeraMapper mapper;

    @Override
    public Optional<GuaraniPropuestaTipoChequera> findById(Integer id) {
        return repository.findById(id).map(mapper::toDomainModel);
    }

    @Override
    public Optional<GuaraniPropuestaTipoChequera> findByPropuestaGuaraniAndLectivoId(
            Integer propuestaGuarani, Integer lectivoId) {
        return repository.findByPropuestaGuaraniAndLectivoId(propuestaGuarani, lectivoId)
                .map(mapper::toDomainModel);
    }

    @Override
    public GuaraniPropuestaTipoChequera save(GuaraniPropuestaTipoChequera domain) {
        GuaraniPropuestaTipoChequeraEntity entity = mapper.toEntity(domain);
        return mapper.toDomainModel(repository.save(entity));
    }

    @Override
    public boolean existsById(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}
