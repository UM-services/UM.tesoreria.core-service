package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.model.LimiteAutorizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.web.dto.CompraAutoridadUsuarioResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.web.dto.LimiteAutorizacionResponse;

import java.util.List;

@Component
public class CompraAutoridadUsuarioDtoMapper {

    public CompraAutoridadUsuarioResponse toResponse(Integer usuarioId, List<Long> autoridadPerfilIds) {
        return CompraAutoridadUsuarioResponse.builder()
                .usuarioId(usuarioId)
                .autoridadPerfilIds(autoridadPerfilIds)
                .build();
    }

    public LimiteAutorizacionResponse toResponse(LimiteAutorizacion limite) {
        if (limite == null) return null;
        return LimiteAutorizacionResponse.builder()
                .usuarioId(limite.usuarioId())
                .ejercicioId(limite.ejercicioId())
                .multiplico(limite.multiplico())
                .referencia(limite.referencia())
                .limite(limite.limite())
                .ilimitado(limite.ilimitado())
                .tieneAutoridad(limite.tieneAutoridad())
                .build();
    }

}
