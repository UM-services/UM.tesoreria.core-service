package um.tesoreria.core.hexagonal.compras.articulo.domain.model;

/**
 * Tabla que referencia a un artículo. {@code cantidad} es nula cuando solo se conoce la tabla
 * (la informó la base al rechazar el borrado).
 */
public record ReferenciaArticulo(String tabla, Long cantidad) {
}
