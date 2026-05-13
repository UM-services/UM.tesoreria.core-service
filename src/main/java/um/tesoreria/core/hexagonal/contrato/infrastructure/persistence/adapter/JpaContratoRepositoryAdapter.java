package um.tesoreria.core.hexagonal.contrato.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contrato.domain.model.Contrato;
import um.tesoreria.core.hexagonal.contrato.domain.ports.out.ContratoRepository;
import um.tesoreria.core.hexagonal.contrato.infrastructure.persistence.entity.ContratoEntity;
import um.tesoreria.core.hexagonal.contrato.infrastructure.persistence.mapper.ContratoMapper;
import um.tesoreria.core.hexagonal.contrato.infrastructure.persistence.repository.JpaContratoRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaContratoRepositoryAdapter implements ContratoRepository {

    private final JpaContratoRepository jpaContratoRepository;
    private final ContratoMapper contratoMapper;

    @Override
    public Contrato create(Contrato contrato) {
        ContratoEntity entity = contratoMapper.toEntity(contrato);
        ContratoEntity savedEntity = jpaContratoRepository.save(entity);
        return contratoMapper.toDomainModel(savedEntity);
    }

    @Override
    public Optional<Contrato> findById(Long id) {
        return jpaContratoRepository.findByContratoId(id)
                .map(contratoMapper::toDomainModel);
    }

    @Override
    public List<Contrato> findAllByFacultad(Integer facultadId, Integer geograficaId) {
        return jpaContratoRepository.findAllByFacultadIdAndGeograficaId(facultadId, geograficaId).stream()
                .map(contratoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Contrato> findAllAjustable(OffsetDateTime referencia) {
        return jpaContratoRepository.findAllByAjusteAndHastaGreaterThanEqual((byte) 1, referencia).stream()
                .map(contratoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Contrato> findAllVigente(OffsetDateTime referencia) {
        return jpaContratoRepository.findAllByHastaGreaterThanEqual(referencia).stream()
                .map(contratoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Contrato> findAllByPersona(BigDecimal personaId, Integer documentoId) {
        return jpaContratoRepository.findAllByPersonaIdAndDocumentoIdOrderByDesdeDesc(personaId, documentoId).stream()
                .map(contratoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Contrato> update(Long id, Contrato contrato) {
        if (jpaContratoRepository.existsByContratoId(id)) {
            ContratoEntity entity = contratoMapper.toEntity(contrato);
            entity.setContratoId(id); // Ensure the ID is set for update
            ContratoEntity updatedEntity = jpaContratoRepository.save(entity);
            return Optional.of(contratoMapper.toDomainModel(updatedEntity));
        }
        return Optional.empty();
    }

    @Override
    public List<Contrato> saveAll(List<Contrato> contratos) {
        List<ContratoEntity> entities = contratos.stream()
                .map(contratoMapper::toEntity)
                .collect(Collectors.toList());
        List<ContratoEntity> savedEntities = jpaContratoRepository.saveAll(entities);
        return savedEntities.stream()
                .map(contratoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(Long id) {
        if (jpaContratoRepository.existsByContratoId(id)) {
            jpaContratoRepository.deleteByContratoId(id);
            return true;
        }
        return false;
    }
}
