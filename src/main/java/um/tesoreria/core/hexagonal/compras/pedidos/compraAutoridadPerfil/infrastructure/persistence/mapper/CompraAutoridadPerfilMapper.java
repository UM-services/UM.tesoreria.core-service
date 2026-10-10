package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.persistence.entity.CompraAutoridadPerfilEntity;

@Component
public class CompraAutoridadPerfilMapper {

    public CompraAutoridadPerfil toDomain(CompraAutoridadPerfilEntity entity) {
        if (entity == null) return null;
        CompraAutoridadPerfil.CompraAutoridadPerfilBuilder builder = CompraAutoridadPerfil.builder()
                .autoridadPerfilId(entity.getAutoridadPerfilId())
                .nombre(entity.getNombre())
                // multiplico es intencionalmente nullable (nulo = ilimitado): se copia tal cual.
                .multiplico(entity.getMultiplico())
                .created(entity.getCreated())
                .updated(entity.getUpdated());
        if (entity.getActivo() != null) builder.activo(entity.getActivo());
        return builder.build();
    }

    public CompraAutoridadPerfilEntity toEntity(CompraAutoridadPerfil domain) {
        if (domain == null) return null;
        CompraAutoridadPerfilEntity.CompraAutoridadPerfilEntityBuilder builder = CompraAutoridadPerfilEntity.builder()
                .autoridadPerfilId(domain.getAutoridadPerfilId())
                .nombre(domain.getNombre())
                .multiplico(domain.getMultiplico());
        if (domain.getActivo() != null) builder.activo(domain.getActivo());
        return builder.build();
    }

    /**
     * Aplica los campos del dominio sobre una entidad YA MANAGED. {@code multiplico} se setea
     * siempre (permite limpiarlo a nulo = ilimitado); el resto de nulls se ignoran.
     */
    public void updateEntity(CompraAutoridadPerfil domain, CompraAutoridadPerfilEntity entity) {
        if (domain == null || entity == null) return;
        entity.setNombre(domain.getNombre());
        entity.setMultiplico(domain.getMultiplico());
        if (domain.getActivo() != null) entity.setActivo(domain.getActivo());
    }

}
