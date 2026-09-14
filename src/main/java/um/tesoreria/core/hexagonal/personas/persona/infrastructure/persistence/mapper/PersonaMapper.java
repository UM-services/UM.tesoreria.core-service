package um.tesoreria.core.hexagonal.personas.persona.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.persistence.entity.PersonaEntity;

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
                .numeroPrefijo(entity.getNumeroPrefijo())
                .numeroPosfijo(entity.getNumeroPosfijo())
                .guaraniPersona(entity.getGuaraniPersona())
                .build();
    }

    public PersonaEntity toEntity(Persona domain) {
        if (domain == null) return null;
        return PersonaEntity.builder()
                .uniqueId(domain.getUniqueId())
                .personaId(domain.getPersonaId())
                .numeroPrefijo(domain.getNumeroPrefijo())
                .numeroPosfijo(domain.getNumeroPosfijo())
                .documentoId(domain.getDocumentoId())
                .apellido(domain.getApellido())
                .nombre(domain.getNombre())
                .sexo(domain.getSexo())
                .primero(domain.getPrimero())
                .cuit(domain.getCuit())
                .cbu(domain.getCbu())
                .password(domain.getPassword())
                .hpum(domain.getHpum())
                .guaraniPersona(domain.getGuaraniPersona())
                .build();
    }
}
