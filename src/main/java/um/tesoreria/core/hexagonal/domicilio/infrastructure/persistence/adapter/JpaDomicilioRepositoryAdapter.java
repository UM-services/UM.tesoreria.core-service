package um.tesoreria.core.hexagonal.domicilio.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.domicilio.domain.ports.out.DomicilioRepository;
import um.tesoreria.core.hexagonal.domicilio.infrastructure.persistence.entity.DomicilioEntity;
import um.tesoreria.core.hexagonal.domicilio.infrastructure.persistence.mapper.DomicilioMapper;
import um.tesoreria.core.hexagonal.domicilio.infrastructure.persistence.repository.JpaDomicilioRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaDomicilioRepositoryAdapter implements DomicilioRepository {

    private final JpaDomicilioRepository jpaDomicilioRepository;
    private final DomicilioMapper domicilioMapper;

    @Override
    public Domicilio create(Domicilio domicilio) {
        domicilio.setFecha(OffsetDateTime.now());
        DomicilioEntity entity = domicilioMapper.toEntity(domicilio);
        DomicilioEntity saved = jpaDomicilioRepository.save(entity);
        return domicilioMapper.toDomainModel(saved);
    }

    @Override
    public Optional<Domicilio> findById(Long id) {
        return jpaDomicilioRepository.findById(id).map(domicilioMapper::toDomainModel);
    }

    @Override
    public Optional<Domicilio> findByUnique(BigDecimal personaId, Integer documentoId) {
        return jpaDomicilioRepository.findByPersonaIdAndDocumentoId(personaId, documentoId)
                .map(domicilioMapper::toDomainModel);
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
            entity.setEmailPersonal(domicilio.getEmailPersonal());
            entity.setEmailInstitucional(domicilio.getEmailInstitucional());
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
