package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.web.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.dependencias.geografica.infrastructure.web.mapper.GeograficaDtoMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.model.UsuarioChequeraGeografica;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.web.dto.UsuarioChequeraGeograficaRequest;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.web.dto.UsuarioChequeraGeograficaResponse;

@Component
@RequiredArgsConstructor
public class UsuarioChequeraGeograficaDtoMapper {

    // Excepción cross-slice autorizada: reutiliza el DTO mapper de dependencias.geografica
    // (patrón de los slices hermanos usuarioChequeraClaseChequera / usuarioChequeraFacultad).
    private final GeograficaDtoMapper geograficaDtoMapper;

    public UsuarioChequeraGeografica toDomain(UsuarioChequeraGeograficaRequest request) {
        if (request == null) return null;
        return UsuarioChequeraGeografica.builder()
                .userId(request.getUserId())
                .geograficaId(request.getGeograficaId())
                .build();
    }

    public UsuarioChequeraGeograficaResponse toResponse(UsuarioChequeraGeografica domain) {
        if (domain == null) return null;
        return UsuarioChequeraGeograficaResponse.builder()
                .usuarioChequeraGeograficaId(domain.getUsuarioChequeraGeograficaId())
                .userId(domain.getUserId())
                .geograficaId(domain.getGeograficaId())
                .created(domain.getCreated())
                .updated(domain.getUpdated())
                .geografica(geograficaDtoMapper.toResponse(domain.getGeografica()))
                .build();
    }
}
