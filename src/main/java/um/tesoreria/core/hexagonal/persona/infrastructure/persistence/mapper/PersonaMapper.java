package um.tesoreria.core.hexagonal.persona.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.entity.PersonaEntity;

@Component
public class PersonaMapper {

    public Persona toDomainModel(PersonaEntity entity) {
        if (entity == null) return null;
        return Persona.builder()
                .uniqueId(entity.getUniqueId())
                .personaId(entity.getPersonaId())
                .documentoId(entity.getDocumentoId())
                .apellido(entity.getApellido())
                .nombre(entity.getNombre())
                .sexo(entity.getSexo())
                .primero(entity.getPrimero())
                .cuit(entity.getCuit())
                .cbu(entity.getCbu())
                .password(entity.getPassword())
                .hpum(entity.getHpum())
                .build();
    }

    public PersonaEntity toEntity(Persona domain) {
        if (domain == null) return null;
        PersonaEntity entity = new PersonaEntity();
        entity.setUniqueId(domain.getUniqueId());
        entity.setPersonaId(domain.getPersonaId());
        entity.setDocumentoId(domain.getDocumentoId());
        entity.setApellido(domain.getApellido());
        entity.setNombre(domain.getNombre());
        entity.setSexo(domain.getSexo());
        entity.setPrimero(domain.getPrimero());
        entity.setCuit(domain.getCuit());
        entity.setCbu(domain.getCbu());
        entity.setPassword(domain.getPassword());
        entity.setHpum(domain.getHpum());
        return entity;
    }
}
