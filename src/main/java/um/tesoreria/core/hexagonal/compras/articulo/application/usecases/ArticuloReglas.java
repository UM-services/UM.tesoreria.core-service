package um.tesoreria.core.hexagonal.compras.articulo.application.usecases;

import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloValidationException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;

import java.util.Set;

/**
 * Reglas de entrada de artículo. Los límites salen de las columnas de {@code articulos} en dev:
 * {@code Art_ID int}, {@code Art_Tipo enum('bien','gasto')}, {@code Art_Nombre varchar(150)},
 * {@code Art_Descripcion varchar(64)}, {@code Art_Unidad varchar(16)}.
 * El nombre vacío y {@code numeroCuenta} nulo se aceptan (hay artículos así en dev).
 */
final class ArticuloReglas {

    static final Set<String> TIPOS = Set.of("bien", "gasto");
    static final long ID_MAX = Integer.MAX_VALUE;
    static final int NOMBRE_MAX = 150;
    static final int DESCRIPCION_MAX = 64;
    static final int UNIDAD_MAX = 16;

    private ArticuloReglas() {
    }

    static void validarAlta(Articulo articulo) {
        var id = articulo.getArticuloId();
        // Cero tampoco: con AUTO_INCREMENT, MySQL lo reemplazaría por el siguiente id
        if (id == null || id < 1 || id > ID_MAX) {
            throw new ArticuloValidationException("articuloId", "articuloId es obligatorio y debe estar entre 1 y " + ID_MAX + ".");
        }
        if (articulo.getTipo() == null) {
            throw new ArticuloValidationException("tipo", "tipo es obligatorio: 'bien' o 'gasto'.");
        }
        validarCampos(articulo);
    }

    /** En una edición, un campo nulo significa "sin cambios": solo se validan los que vienen. */
    static void validarCambios(Articulo cambios) {
        validarCampos(cambios);
    }

    private static void validarCampos(Articulo articulo) {
        if (articulo.getTipo() != null && !TIPOS.contains(articulo.getTipo())) {
            throw new ArticuloValidationException("tipo", "tipo debe ser 'bien' o 'gasto'.");
        }
        validarBandera("directo", articulo.getDirecto());
        validarBandera("habilitado", articulo.getHabilitado());
        validarLargo("nombre", articulo.getNombre(), NOMBRE_MAX);
        validarLargo("descripcion", articulo.getDescripcion(), DESCRIPCION_MAX);
        validarLargo("unidad", articulo.getUnidad(), UNIDAD_MAX);
    }

    private static void validarBandera(String campo, Byte valor) {
        if (valor != null && valor != 0 && valor != 1) {
            throw new ArticuloValidationException(campo, campo + " debe ser 0 o 1.");
        }
    }

    private static void validarLargo(String campo, String valor, int maximo) {
        if (valor != null && valor.length() > maximo) {
            throw new ArticuloValidationException(campo, campo + " admite hasta " + maximo + " caracteres.");
        }
    }

}
