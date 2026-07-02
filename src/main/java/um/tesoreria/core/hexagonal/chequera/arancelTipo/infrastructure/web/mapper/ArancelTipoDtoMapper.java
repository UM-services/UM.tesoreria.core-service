package um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.model.ArancelTipo;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.web.dto.ArancelTipoRequest;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.web.dto.ArancelTipoResponse;

@Component
public class ArancelTipoDtoMapper {

    public ArancelTipo toDomain(ArancelTipoRequest request) {
        if (request == null) return null;
        return ArancelTipo.builder()
                .arancelTipoId(request.getArancelTipoId())
                .descripcion(request.getDescripcion())
                .medioArancel(request.getMedioArancel())
                .arancelTipoIdCompleto(request.getArancelTipoIdCompleto())
                .habilitado(request.getHabilitado())
                .build();
    }

    public ArancelTipoResponse toResponse(ArancelTipo domain) {
        if (domain == null) return null;
        return ArancelTipoResponse.builder()
                .arancelTipoId(domain.getArancelTipoId())
                .descripcion(domain.getDescripcion())
                .medioArancel(domain.getMedioArancel())
                .arancelTipoIdCompleto(domain.getArancelTipoIdCompleto())
                .habilitado(domain.getHabilitado())
                .build();
    }
}
