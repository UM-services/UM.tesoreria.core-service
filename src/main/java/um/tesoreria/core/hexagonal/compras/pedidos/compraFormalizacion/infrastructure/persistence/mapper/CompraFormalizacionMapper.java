package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.entity.CompraFormalizacionEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.entity.CompraFormalizacionItemEntity;

@Component
public class CompraFormalizacionMapper {

    // ---- Cabecera ----

    public CompraFormalizacionEntity toEntity(CompraFormalizacion domain) {
        if (domain == null) return null;
        return CompraFormalizacionEntity.builder()
                .compraFormalizacionId(domain.getCompraFormalizacionId())
                .compraAdjudicacionId(domain.getCompraAdjudicacionId())
                .numero(domain.getNumero())
                .numeroExterno(domain.getNumeroExterno())
                .ejercicioId(domain.getEjercicioId())
                .fecha(domain.getFecha())
                .proveedorId(domain.getProveedorId())
                .moneda(domain.getMoneda())
                .monto(domain.getMonto())
                .condicionPago(domain.getCondicionPago())
                .plazoEntrega(domain.getPlazoEntrega())
                .garantia(domain.getGarantia())
                .objeto(domain.getObjeto())
                .observacion(domain.getObservacion())
                .usuarioId(domain.getUsuarioId())
                .build();
    }

    public void updateEntity(CompraFormalizacion domain, CompraFormalizacionEntity entity) {
        if (domain == null || entity == null) return;
        entity.setCompraAdjudicacionId(domain.getCompraAdjudicacionId());
        entity.setNumero(domain.getNumero());
        entity.setNumeroExterno(domain.getNumeroExterno());
        entity.setEjercicioId(domain.getEjercicioId());
        entity.setFecha(domain.getFecha());
        entity.setProveedorId(domain.getProveedorId());
        entity.setMoneda(domain.getMoneda());
        entity.setMonto(domain.getMonto());
        entity.setCondicionPago(domain.getCondicionPago());
        entity.setPlazoEntrega(domain.getPlazoEntrega());
        entity.setGarantia(domain.getGarantia());
        entity.setObjeto(domain.getObjeto());
        entity.setObservacion(domain.getObservacion());
        entity.setUsuarioId(domain.getUsuarioId());
    }

    public CompraFormalizacion toDomain(CompraFormalizacionEntity entity) {
        if (entity == null) return null;
        return CompraFormalizacion.builder()
                .compraFormalizacionId(entity.getCompraFormalizacionId())
                .compraAdjudicacionId(entity.getCompraAdjudicacionId())
                .numero(entity.getNumero())
                .numeroExterno(entity.getNumeroExterno())
                .ejercicioId(entity.getEjercicioId())
                .fecha(entity.getFecha())
                .proveedorId(entity.getProveedorId())
                .moneda(entity.getMoneda())
                .monto(entity.getMonto())
                .condicionPago(entity.getCondicionPago())
                .plazoEntrega(entity.getPlazoEntrega())
                .garantia(entity.getGarantia())
                .objeto(entity.getObjeto())
                .observacion(entity.getObservacion())
                .usuarioId(entity.getUsuarioId())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .build();
    }

    // ---- Ítems ----

    public CompraFormalizacionItemEntity itemToEntity(CompraFormalizacionItem domain) {
        if (domain == null) return null;
        return CompraFormalizacionItemEntity.builder()
                .compraFormalizacionItemId(domain.getCompraFormalizacionItemId())
                .compraFormalizacionId(domain.getCompraFormalizacionId())
                .compraCotizacionItemId(domain.getCompraCotizacionItemId())
                .compraPedidoItemId(domain.getCompraPedidoItemId())
                .orden(domain.getOrden())
                .cantidad(domain.getCantidad())
                .unidad(domain.getUnidad())
                .descripcion(domain.getDescripcion())
                .especificaciones(domain.getEspecificaciones())
                .precioUnitario(domain.getPrecioUnitario())
                .subtotal(domain.getSubtotal())
                .marca(domain.getMarca())
                .build();
    }

    public CompraFormalizacionItem itemToDomain(CompraFormalizacionItemEntity entity) {
        if (entity == null) return null;
        return CompraFormalizacionItem.builder()
                .compraFormalizacionItemId(entity.getCompraFormalizacionItemId())
                .compraFormalizacionId(entity.getCompraFormalizacionId())
                .compraCotizacionItemId(entity.getCompraCotizacionItemId())
                .compraPedidoItemId(entity.getCompraPedidoItemId())
                .orden(entity.getOrden())
                .cantidad(entity.getCantidad())
                .unidad(entity.getUnidad())
                .descripcion(entity.getDescripcion())
                .especificaciones(entity.getEspecificaciones())
                .precioUnitario(entity.getPrecioUnitario())
                .subtotal(entity.getSubtotal())
                .marca(entity.getMarca())
                .build();
    }

}
