package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.dependencias.geografica.infrastructure.persistence.mapper.GeograficaMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.model.UsuarioChequeraGeografica;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.persistence.entity.UsuarioChequeraGeograficaEntity;

@Component
@RequiredArgsConstructor
public class UsuarioChequeraGeograficaMapper {

    // Excepción cross-slice autorizada: reutiliza el mapper de dependencias.geografica
    // (patrón de los slices hermanos usuarioChequeraClaseChequera / usuarioChequeraFacultad).
    private final GeograficaMapper geograficaMapper;

    public UsuarioChequeraGeografica toDomain(UsuarioChequeraGeograficaEntity entity) {
        if (entity == null) return null;
        return UsuarioChequeraGeografica.builder()
                .usuarioChequeraGeograficaId(entity.getUsuarioChequeraGeograficaId())
                .userId(entity.getUserId())
                .geograficaId(entity.getGeograficaId())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .geografica(geograficaMapper.toDomainModel(entity.getGeografica()))
                .build();
    }

    public UsuarioChequeraGeograficaEntity toEntity(UsuarioChequeraGeografica domain) {
        if (domain == null) return null;
        return UsuarioChequeraGeograficaEntity.builder()
                .usuarioChequeraGeograficaId(domain.getUsuarioChequeraGeograficaId())
                .userId(domain.getUserId())
                .geograficaId(domain.getGeograficaId())
                .build();
    }
}
