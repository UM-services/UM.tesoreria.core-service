package um.tesoreria.core.hexagonal.persona.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.persona.domain.ports.out.PersonaRepository;
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.entity.PersonaEntity;
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.mapper.PersonaMapper;
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.repository.JpaPersonaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaPersonaRepositoryAdapter implements PersonaRepository {

    private final JpaPersonaRepository jpaPersonaRepository;
    private final PersonaMapper personaMapper;

    @Override
    public Optional<Persona> findByPersonaIdAndDocumentoId(BigDecimal personaId, Integer documentoId) {
        return jpaPersonaRepository.findByPersonaIdAndDocumentoId(personaId, documentoId)
                .map(personaMapper::toDomainModel);
    }

    @Override
    public Optional<Persona> findTopByPersonaId(BigDecimal personaId) {
        return jpaPersonaRepository.findTopByPersonaId(personaId)
                .map(personaMapper::toDomainModel);
    }

    @Override
    public Optional<Persona> findByUniqueId(Long uniqueId) {
        return jpaPersonaRepository.findByUniqueId(uniqueId)
                .map(personaMapper::toDomainModel);
    }

    @Override
    public List<Persona> findAllByCbuLike(String cbu) {
        return jpaPersonaRepository.findAllByCbuLike(cbu).stream()
                .map(personaMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Persona save(Persona persona) {
        PersonaEntity entity = personaMapper.toEntity(persona);
        PersonaEntity saved = jpaPersonaRepository.save(entity);
        return personaMapper.toDomainModel(saved);
    }
}
