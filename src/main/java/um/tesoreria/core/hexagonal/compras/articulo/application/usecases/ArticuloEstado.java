package um.tesoreria.core.hexagonal.compras.articulo.application.usecases;

import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Foto de un artículo para el historial #404: solo los campos de la fila, sin la cuenta asociada.
 * Precio y cuenta con la escala de su columna, así dos fotos de la misma fila son iguales.
 */
record ArticuloEstado(Long articuloId, String nombre, String descripcion, String unidad, BigDecimal precio,
                      Byte inventariable, Long stockMinimo, BigDecimal numeroCuenta, String tipo, Byte directo,
                      Byte habilitado) {

    static final String ENTIDAD = "articulo";

    static ArticuloEstado de(Articulo articulo) {
        return new ArticuloEstado(articulo.getArticuloId(), articulo.getNombre(), articulo.getDescripcion(),
                articulo.getUnidad(), escala(articulo.getPrecio(), ArticuloReglas.PRECIO_DECIMALES),
                articulo.getInventariable(), articulo.getStockMinimo(), escala(articulo.getNumeroCuenta(), 0),
                articulo.getTipo(), articulo.getDirecto(), articulo.getHabilitado());
    }

    String clave() {
        return String.valueOf(articuloId);
    }

    private static BigDecimal escala(BigDecimal valor, int decimales) {
        return valor == null ? null : valor.setScale(decimales, RoundingMode.UNNECESSARY);
    }
}
