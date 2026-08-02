package um.tesoreria.core.hexagonal.persona.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import um.tesoreria.core.hexagonal.persona.domain.model.PersonaKey;
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.entity.PersonaKeyEntity;

@Component
public class PersonaKeyMapper {

    public PersonaKey toDomain(PersonaKeyEntity entity) {
        if (entity == null) {
            return null;
        }
        return PersonaKey.builder()
                .unified(entity.getUnified())
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
                .search(entity.getSearch())
                .mark_facultad(entity.getMark_facultad())
                .build();
    }
}
