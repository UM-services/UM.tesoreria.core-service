package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.web.dto.CompraAutoridadPerfilRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.web.dto.CompraAutoridadPerfilResponse;

@Component
public class CompraAutoridadPerfilDtoMapper {

    public CompraAutoridadPerfil toDomain(CompraAutoridadPerfilRequest request) {
        if (request == null) return null;
        CompraAutoridadPerfil.CompraAutoridadPerfilBuilder builder = CompraAutoridadPerfil.builder()
                .nombre(request.getNombre())
                .multiplico(request.getMultiplico());
        if (request.getActivo() != null) builder.activo(request.getActivo());
        return builder.build();
    }

    public CompraAutoridadPerfilResponse toResponse(CompraAutoridadPerfil domain) {
        if (domain == null) return null;
        return CompraAutoridadPerfilResponse.builder()
                .autoridadPerfilId(domain.getAutoridadPerfilId())
                .nombre(domain.getNombre())
                .multiplico(domain.getMultiplico())
                .ilimitado(domain.getMultiplico() == null)
                .activo(domain.getActivo())
                .created(domain.getCreated())
                .updated(domain.getUpdated())
                .build();
    }

}
