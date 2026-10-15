package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionPatrimonio;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionResultado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.entity.CompraRecepcionEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.entity.CompraRecepcionItemEntity;

@Component
public class CompraRecepcionMapper {

    // ---- Cabecera ----

    public CompraRecepcionEntity toEntity(CompraRecepcion domain) {
        if (domain == null) return null;
        return CompraRecepcionEntity.builder()
                .compraRecepcionId(domain.getCompraRecepcionId())
                .compraFormalizacionId(domain.getCompraFormalizacionId())
                .numero(domain.getNumero())
                .ejercicioId(domain.getEjercicioId())
                .fecha(domain.getFecha())
                .remito(domain.getRemito())
                .resultado(domain.getResultado() != null ? domain.getResultado().name() : null)
                .patrimonio(domain.getPatrimonio() != null ? domain.getPatrimonio().name() : null)
                .observacion(domain.getObservacion())
                .usuarioId(domain.getUsuarioId())
                .build();
    }

    public void updateEntity(CompraRecepcion domain, CompraRecepcionEntity entity) {
        if (domain == null || entity == null) return;
        entity.setCompraFormalizacionId(domain.getCompraFormalizacionId());
        entity.setNumero(domain.getNumero());
        entity.setEjercicioId(domain.getEjercicioId());
        entity.setFecha(domain.getFecha());
        entity.setRemito(domain.getRemito());
        entity.setResultado(domain.getResultado() != null ? domain.getResultado().name() : null);
        entity.setPatrimonio(domain.getPatrimonio() != null ? domain.getPatrimonio().name() : null);
        entity.setObservacion(domain.getObservacion());
        entity.setUsuarioId(domain.getUsuarioId());
    }

    public CompraRecepcion toDomain(CompraRecepcionEntity entity) {
        if (entity == null) return null;
        return CompraRecepcion.builder()
                .compraRecepcionId(entity.getCompraRecepcionId())
                .compraFormalizacionId(entity.getCompraFormalizacionId())
                .numero(entity.getNumero())
                .ejercicioId(entity.getEjercicioId())
                .fecha(entity.getFecha())
                .remito(entity.getRemito())
                .resultado(entity.getResultado() != null
                        ? CompraRecepcionResultado.valueOf(entity.getResultado()) : null)
                .patrimonio(entity.getPatrimonio() != null
                        ? CompraRecepcionPatrimonio.valueOf(entity.getPatrimonio()) : null)
                .observacion(entity.getObservacion())
                .usuarioId(entity.getUsuarioId())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .build();
    }

    // ---- Ítems ----

    public CompraRecepcionItemEntity itemToEntity(CompraRecepcionItem domain) {
        if (domain == null) return null;
        return CompraRecepcionItemEntity.builder()
                .compraRecepcionItemId(domain.getCompraRecepcionItemId())
                .compraRecepcionId(domain.getCompraRecepcionId())
                .compraFormalizacionItemId(domain.getCompraFormalizacionItemId())
                .orden(domain.getOrden())
                .cantidadRecibida(domain.getCantidadRecibida())
                .resultado(domain.getResultado() != null ? domain.getResultado().name() : null)
                .observacion(domain.getObservacion())
                .build();
    }

    public CompraRecepcionItem itemToDomain(CompraRecepcionItemEntity entity) {
        if (entity == null) return null;
        return CompraRecepcionItem.builder()
                .compraRecepcionItemId(entity.getCompraRecepcionItemId())
                .compraRecepcionId(entity.getCompraRecepcionId())
                .compraFormalizacionItemId(entity.getCompraFormalizacionItemId())
                .orden(entity.getOrden())
                .cantidadRecibida(entity.getCantidadRecibida())
                .resultado(entity.getResultado() != null
                        ? CompraRecepcionResultado.valueOf(entity.getResultado()) : null)
                .observacion(entity.getObservacion())
                .build();
    }

}
