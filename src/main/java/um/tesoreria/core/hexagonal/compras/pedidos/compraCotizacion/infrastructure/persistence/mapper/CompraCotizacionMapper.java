package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedor;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedorEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedorItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity.CompraCotizacionEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity.CompraCotizacionItemEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity.CompraCotizacionProveedorEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity.CompraCotizacionProveedorItemEntity;

@Component
public class CompraCotizacionMapper {

    // ---- Cabecera ----

    public CompraCotizacionEntity toEntity(CompraCotizacion domain) {
        if (domain == null) return null;
        CompraCotizacionEntity entity = new CompraCotizacionEntity();
        entity.setCompraCotizacionId(domain.getCompraCotizacionId());
        copy(domain, entity);
        return entity;
    }

    public void updateEntity(CompraCotizacion domain, CompraCotizacionEntity entity) {
        copy(domain, entity);
    }

    private void copy(CompraCotizacion domain, CompraCotizacionEntity entity) {
        entity.setNumero(domain.getNumero());
        entity.setEjercicioId(domain.getEjercicioId());
        entity.setFecha(domain.getFecha());
        entity.setEstado(domain.getEstado() != null ? domain.getEstado().name() : null);
        entity.setObservacion(domain.getObservacion());
        entity.setFechaLimiteRespuesta(domain.getFechaLimiteRespuesta());
        entity.setMontoEstimado(domain.getMontoEstimado());
        entity.setCantidadMinima(domain.getCantidadMinima());
        entity.setExcepcionMotivo(domain.getExcepcionMotivo());
        entity.setFechaEnvio(domain.getFechaEnvio());
        entity.setProveedorRecomendadoId(domain.getProveedorRecomendadoId());
        entity.setFundamentoRecomendacion(domain.getFundamentoRecomendacion());
        entity.setFechaRecomendacion(domain.getFechaRecomendacion());
        entity.setUsuarioId(domain.getUsuarioId());
    }

    public CompraCotizacion toDomain(CompraCotizacionEntity entity) {
        if (entity == null) return null;
        return CompraCotizacion.builder()
                .compraCotizacionId(entity.getCompraCotizacionId())
                .numero(entity.getNumero())
                .ejercicioId(entity.getEjercicioId())
                .fecha(entity.getFecha())
                .estado(entity.getEstado() != null ? CompraCotizacionEstado.valueOf(entity.getEstado()) : null)
                .observacion(entity.getObservacion())
                .fechaLimiteRespuesta(entity.getFechaLimiteRespuesta())
                .montoEstimado(entity.getMontoEstimado())
                .cantidadMinima(entity.getCantidadMinima())
                .excepcionMotivo(entity.getExcepcionMotivo())
                .fechaEnvio(entity.getFechaEnvio())
                .proveedorRecomendadoId(entity.getProveedorRecomendadoId())
                .fundamentoRecomendacion(entity.getFundamentoRecomendacion())
                .fechaRecomendacion(entity.getFechaRecomendacion())
                .usuarioId(entity.getUsuarioId())
                .build();
    }

    // ---- Ítems ----

    public CompraCotizacionItemEntity itemToEntity(CompraCotizacionItem domain) {
        if (domain == null) return null;
        return CompraCotizacionItemEntity.builder()
                .compraCotizacionItemId(domain.getCompraCotizacionItemId())
                .compraCotizacionId(domain.getCompraCotizacionId())
                .compraPedidoItemId(domain.getCompraPedidoItemId())
                .orden(domain.getOrden())
                .cantidad(domain.getCantidad())
                .build();
    }

    public CompraCotizacionItem itemToDomain(CompraCotizacionItemEntity entity) {
        if (entity == null) return null;
        return CompraCotizacionItem.builder()
                .compraCotizacionItemId(entity.getCompraCotizacionItemId())
                .compraCotizacionId(entity.getCompraCotizacionId())
                .compraPedidoItemId(entity.getCompraPedidoItemId())
                .orden(entity.getOrden())
                .cantidad(entity.getCantidad())
                .build();
    }

    // ---- Proveedores ----

    public CompraCotizacionProveedorEntity proveedorToEntity(CompraCotizacionProveedor domain) {
        if (domain == null) return null;
        return CompraCotizacionProveedorEntity.builder()
                .compraCotizacionProveedorId(domain.getCompraCotizacionProveedorId())
                .compraCotizacionId(domain.getCompraCotizacionId())
                .proveedorId(domain.getProveedorId())
                .email(domain.getEmail())
                .estado(domain.getEstado() != null ? domain.getEstado().name() : null)
                .fechaEnvio(domain.getFechaEnvio())
                .observacion(domain.getObservacion())
                .fechaRespuesta(domain.getFechaRespuesta())
                .moneda(domain.getMoneda())
                .total(domain.getTotal())
                .plazoEntrega(domain.getPlazoEntrega())
                .condicionPago(domain.getCondicionPago())
                .garantia(domain.getGarantia())
                .validezOferta(domain.getValidezOferta())
                .build();
    }

    public CompraCotizacionProveedor proveedorToDomain(CompraCotizacionProveedorEntity entity) {
        if (entity == null) return null;
        return CompraCotizacionProveedor.builder()
                .compraCotizacionProveedorId(entity.getCompraCotizacionProveedorId())
                .compraCotizacionId(entity.getCompraCotizacionId())
                .proveedorId(entity.getProveedorId())
                .email(entity.getEmail())
                .estado(entity.getEstado() != null ? CompraCotizacionProveedorEstado.valueOf(entity.getEstado()) : null)
                .fechaEnvio(entity.getFechaEnvio())
                .observacion(entity.getObservacion())
                .fechaRespuesta(entity.getFechaRespuesta())
                .moneda(entity.getMoneda())
                .total(entity.getTotal())
                .plazoEntrega(entity.getPlazoEntrega())
                .condicionPago(entity.getCondicionPago())
                .garantia(entity.getGarantia())
                .validezOferta(entity.getValidezOferta())
                .build();
    }

    public void updateProveedorEntity(CompraCotizacionProveedor domain, CompraCotizacionProveedorEntity entity) {
        entity.setProveedorId(domain.getProveedorId());
        entity.setEmail(domain.getEmail());
        entity.setEstado(domain.getEstado() != null ? domain.getEstado().name() : null);
        entity.setFechaEnvio(domain.getFechaEnvio());
        entity.setObservacion(domain.getObservacion());
        entity.setFechaRespuesta(domain.getFechaRespuesta());
        entity.setMoneda(domain.getMoneda());
        entity.setTotal(domain.getTotal());
        entity.setPlazoEntrega(domain.getPlazoEntrega());
        entity.setCondicionPago(domain.getCondicionPago());
        entity.setGarantia(domain.getGarantia());
        entity.setValidezOferta(domain.getValidezOferta());
    }

    // ---- Detalle de la respuesta ----

    public CompraCotizacionProveedorItemEntity proveedorItemToEntity(CompraCotizacionProveedorItem domain) {
        if (domain == null) return null;
        return CompraCotizacionProveedorItemEntity.builder()
                .compraCotizacionProveedorItemId(domain.getCompraCotizacionProveedorItemId())
                .compraCotizacionProveedorId(domain.getCompraCotizacionProveedorId())
                .compraCotizacionItemId(domain.getCompraCotizacionItemId())
                .cotiza(domain.getCotiza())
                .precioUnitario(domain.getPrecioUnitario())
                .subtotal(domain.getSubtotal())
                .marca(domain.getMarca())
                .observacion(domain.getObservacion())
                .build();
    }

    public CompraCotizacionProveedorItem proveedorItemToDomain(CompraCotizacionProveedorItemEntity entity) {
        if (entity == null) return null;
        return CompraCotizacionProveedorItem.builder()
                .compraCotizacionProveedorItemId(entity.getCompraCotizacionProveedorItemId())
                .compraCotizacionProveedorId(entity.getCompraCotizacionProveedorId())
                .compraCotizacionItemId(entity.getCompraCotizacionItemId())
                .cotiza(entity.getCotiza())
                .precioUnitario(entity.getPrecioUnitario())
                .subtotal(entity.getSubtotal())
                .marca(entity.getMarca())
                .observacion(entity.getObservacion())
                .build();
    }
}
