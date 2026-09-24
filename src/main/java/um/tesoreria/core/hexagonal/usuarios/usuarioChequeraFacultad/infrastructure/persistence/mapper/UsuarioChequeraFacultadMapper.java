package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.dependencias.facultad.infrastructure.persistence.mapper.FacultadMapper;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.mapper.UsuarioMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.model.UsuarioChequeraFacultad;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.persistence.entity.UsuarioChequeraFacultadEntity;

@Component
@RequiredArgsConstructor
public class UsuarioChequeraFacultadMapper {

    private final UsuarioMapper usuarioMapper;
    private final FacultadMapper facultadMapper;

    public UsuarioChequeraFacultad toDomain(UsuarioChequeraFacultadEntity entity) {
        if (entity == null) return null;
        return UsuarioChequeraFacultad.builder()
                .usuarioChequeraFacultadId(entity.getUsuarioChequeraFacultadId())
                .userId(entity.getUserId())
                .facultadId(entity.getFacultadId())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .usuario(usuarioMapper.toDomainModel(entity.getUsuario()))
                .facultad(facultadMapper.toDomain(entity.getFacultad()))
                .build();
    }

    public UsuarioChequeraFacultadEntity toEntity(UsuarioChequeraFacultad domain) {
        if (domain == null) return null;
        return UsuarioChequeraFacultadEntity.builder()
                .usuarioChequeraFacultadId(domain.getUsuarioChequeraFacultadId())
                .userId(domain.getUserId())
                .facultadId(domain.getFacultadId())
                .build();
    }
}
