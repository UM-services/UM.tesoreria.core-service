package um.tesoreria.core.hexagonal.personas.legajo.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import um.tesoreria.core.hexagonal.personas.legajo.domain.model.Legajo;
import um.tesoreria.core.hexagonal.personas.legajo.domain.ports.out.LegajoRepository;
import um.tesoreria.core.hexagonal.personas.legajo.infrastructure.persistence.entity.LegajoEntity;
import um.tesoreria.core.hexagonal.personas.legajo.infrastructure.persistence.mapper.LegajoMapper;
import um.tesoreria.core.hexagonal.personas.legajo.infrastructure.persistence.repository.JpaLegajoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaLegajoRepositoryAdapter implements LegajoRepository {

    private final JpaLegajoRepository jpaLegajoRepository;
    private final LegajoMapper mapper;

    @Override
    public List<Legajo> findAllByFacultadId(Integer facultadId) {
        return mapper.toDomain(jpaLegajoRepository.findAllByFacultadId(facultadId));
    }

    @Override
    public Optional<Legajo> findByFacultadIdAndPersonaIdAndDocumentoId(Integer facultadId, BigDecimal personaId,
                                                                       Integer documentoId) {
        return jpaLegajoRepository.findByFacultadIdAndPersonaIdAndDocumentoId(facultadId, personaId, documentoId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Legajo> saveAll(List<Legajo> legajos) {
        List<LegajoEntity> entities = mapper.toEntity(legajos);
        return mapper.toDomain(jpaLegajoRepository.saveAll(entities));
    }

    @Override
    public Legajo save(Legajo legajo) {
        LegajoEntity entity = mapper.toEntity(legajo);
        LegajoEntity saved = jpaLegajoRepository.save(entity);
        return mapper.toDomain(saved);
    }
}