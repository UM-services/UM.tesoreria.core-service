package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.web.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.dependencias.facultad.infrastructure.web.mapper.FacultadDtoMapper;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.mapper.UsuarioDtoMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.model.UsuarioChequeraFacultad;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.web.dto.UsuarioChequeraFacultadResponse;

@Component
@RequiredArgsConstructor
public class UsuarioChequeraFacultadDtoMapper {

    private final UsuarioDtoMapper usuarioDtoMapper;
    private final FacultadDtoMapper facultadDtoMapper;

    public UsuarioChequeraFacultadResponse toResponse(UsuarioChequeraFacultad domain) {
        if (domain == null) return null;
        return UsuarioChequeraFacultadResponse.builder()
                .usuarioChequeraFacultadId(domain.getUsuarioChequeraFacultadId())
                .userId(domain.getUserId())
                .facultadId(domain.getFacultadId())
                .created(domain.getCreated())
                .updated(domain.getUpdated())
                .usuario(usuarioDtoMapper.toResponse(domain.getUsuario()))
                .facultad(facultadDtoMapper.toResponse(domain.getFacultad()))
                .build();
    }
}
