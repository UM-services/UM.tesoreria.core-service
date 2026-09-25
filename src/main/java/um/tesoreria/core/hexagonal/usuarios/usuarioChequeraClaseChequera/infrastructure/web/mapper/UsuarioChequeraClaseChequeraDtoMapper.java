package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.web.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.web.mapper.ClaseChequeraDtoMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model.UsuarioChequeraClaseChequera;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.web.dto.UsuarioChequeraClaseChequeraResponse;

@Component
@RequiredArgsConstructor
public class UsuarioChequeraClaseChequeraDtoMapper {

    private final ClaseChequeraDtoMapper claseChequeraDtoMapper;

    public UsuarioChequeraClaseChequeraResponse toResponse(UsuarioChequeraClaseChequera domain) {
        if (domain == null) return null;
        return UsuarioChequeraClaseChequeraResponse.builder()
                .usuarioChequeraClaseChequeraId(domain.getUsuarioChequeraClaseChequeraId())
                .userId(domain.getUserId())
                .claseChequeraId(domain.getClaseChequeraId())
                .created(domain.getCreated())
                .updated(domain.getUpdated())
                .claseChequera(claseChequeraDtoMapper.toResponse(domain.getClaseChequera()))
                .build();
    }
}
