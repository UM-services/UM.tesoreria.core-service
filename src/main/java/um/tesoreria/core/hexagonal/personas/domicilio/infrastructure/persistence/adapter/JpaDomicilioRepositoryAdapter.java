package um.tesoreria.core.hexagonal.personas.domicilio.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.ports.out.DomicilioRepository;
import um.tesoreria.core.hexagonal.personas.domicilio.infrastructure.persistence.entity.DomicilioEntity;
import um.tesoreria.core.hexagonal.personas.domicilio.infrastructure.persistence.mapper.DomicilioMapper;
import um.tesoreria.core.hexagonal.personas.domicilio.infrastructure.persistence.repository.JpaDomicilioRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JpaDomicilioRepositoryAdapter implements DomicilioRepository {

    private final JpaDomicilioRepository jpaDomicilioRepository;
    private final DomicilioMapper domicilioMapper;

    @Override
    public Domicilio create(Domicilio domicilio) {
        domicilio.setFecha(OffsetDateTime.now());
        DomicilioEntity entity = domicilioMapper.toEntity(domicilio);
        log.info("ADAPTER[Domicilio] -> save ({}): {}",
                entity.getDomicilioId() == null ? "INSERT" : "MERGE/UPDATE domicilioId=" + entity.getDomicilioId(),
                entity.jsonify());
        DomicilioEntity saved = jpaDomicilioRepository.save(entity);
        log.info("ADAPTER[Domicilio] -> save resultado: {}", saved.jsonify());
        return domicilioMapper.toDomainModel(saved);
    }

    @Override
    public Optional<Domicilio> findById(Long id) {
        return jpaDomicilioRepository.findById(id).map(domicilioMapper::toDomainModel);
    }

    @Override
    public Optional<Domicilio> findByUnique(BigDecimal personaId, Integer documentoId) {
        var result = jpaDomicilioRepository.findByPersonaIdAndDocumentoId(personaId, documentoId)
                .map(domicilioMapper::toDomainModel);
        log.info("ADAPTER[Domicilio] -> findByUnique(personaId={}, documentoId={}) -> {}",
                personaId, documentoId,
                result.map(Domicilio::jsonify).orElse("NO ENCONTRADO"));
        return result;
    }

    @Override
    public Optional<Domicilio> findFirstByPersonaId(BigDecimal personaId) {
        return jpaDomicilioRepository.findFirstByPersonaId(personaId)
                .map(domicilioMapper::toDomainModel);
    }

    @Override
    public Optional<Domicilio> update(Long id, Domicilio domicilio) {
        return jpaDomicilioRepository.findByDomicilioId(id).map(entity -> {
            entity.setPersonaId(domicilio.getPersonaId());
            entity.setDocumentoId(domicilio.getDocumentoId());
            entity.setFecha(OffsetDateTime.now());
            entity.setCalle(domicilio.getCalle());
            entity.setPuerta(domicilio.getPuerta());
            entity.setPiso(domicilio.getPiso());
            entity.setDpto(domicilio.getDpto());
            entity.setTelefono(domicilio.getTelefono());
            entity.setMovil(domicilio.getMovil());
            entity.setObservaciones(domicilio.getObservaciones());
            entity.setCodigoPostal(domicilio.getCodigoPostal());
            entity.setFacultadId(domicilio.getFacultadId());
            entity.setProvinciaId(domicilio.getProvinciaId());
            entity.setLocalidadId(domicilio.getLocalidadId());
            entity.setEmailPersonal(domicilio.getEmailPersonal() != null ? domicilio.getEmailPersonal() : "");
            entity.setEmailInstitucional(domicilio.getEmailInstitucional() != null ? domicilio.getEmailInstitucional() : "");
            entity.setLaboral(domicilio.getLaboral());
            DomicilioEntity updated = jpaDomicilioRepository.save(entity);
            return domicilioMapper.toDomainModel(updated);
        });
    }

    @Override
    public boolean deleteById(Long id) {
        if (jpaDomicilioRepository.existsById(id)) {
            jpaDomicilioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
