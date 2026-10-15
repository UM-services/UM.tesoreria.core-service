package um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.articulo.domain.model.ReferenciaArticulo;

import java.util.List;

/**
 * Tablas de negocio que usan un artículo, con su cantidad de filas; vacía si ninguna. Los vínculos
 * {@code ubicacion_articulo} no cuentan: son del artículo y se borran con él.
 */
public interface ReferenciasArticuloRepository {
    List<ReferenciaArticulo> findReferencias(Long articuloId);
}
