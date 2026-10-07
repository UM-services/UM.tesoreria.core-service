package um.tesoreria.core.hexagonal.compras.articulo.application.usecases;

import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloValidationException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

/**
 * Reglas de entrada de artículo. Los límites salen de las columnas de {@code articulos} en dev:
 * {@code Art_ID int}, {@code Art_Tipo enum('bien','gasto')}, {@code Art_Nombre varchar(150)},
 * {@code Art_Descripcion varchar(64)}, {@code Art_Unidad varchar(16)}, {@code Art_Precio decimal(16,2)},
 * {@code Art_Cuenta decimal(11,0)}, {@code Art_StockMinimo int}, banderas {@code tinyint(1)}.
 * Un número que no entra en su columna se rechaza acá: en la base sería un 500, y uno con exponente enorme
 * (por ejemplo 1e999999999) haría que el driver arme un texto gigante. Los decimales de más del precio se
 * redondean a 2, como ya hacía MySQL; en la cuenta se rechazan (redondear apuntaría a otra cuenta).
 * El nombre vacío y {@code numeroCuenta} nulo se aceptan (hay artículos así en dev).
 */
final class ArticuloReglas {

    static final Set<String> TIPOS = Set.of("bien", "gasto");
    static final long ID_MAX = Integer.MAX_VALUE;
    static final int NOMBRE_MAX = 150;
    static final int DESCRIPCION_MAX = 64;
    static final int UNIDAD_MAX = 16;
    static final int PRECIO_ENTEROS = 14;
    static final int PRECIO_DECIMALES = 2;
    static final int CUENTA_DIGITOS = 11;

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
        validarBandera("inventariable", articulo.getInventariable());
        var stock = articulo.getStockMinimo();
        if (stock != null && (stock < Integer.MIN_VALUE || stock > Integer.MAX_VALUE)) {
            throw new ArticuloValidationException("stockMinimo", "stockMinimo debe estar entre " + Integer.MIN_VALUE + " y " + Integer.MAX_VALUE + ".");
        }
        // Primero los enteros sobre el valor tal cual (redondear 1e999999999 sería carísimo), después sobre el
        // redondeado (99999999999999.995 pasa a tener 15 dígitos enteros)
        if (articulo.getPrecio() != null
                && (!entraEnDecimal(articulo.getPrecio(), PRECIO_ENTEROS, Integer.MAX_VALUE)
                    || !entraEnDecimal(redondearPrecio(articulo.getPrecio()), PRECIO_ENTEROS, PRECIO_DECIMALES))) {
            throw new ArticuloValidationException("precio", "precio admite hasta " + PRECIO_ENTEROS + " dígitos enteros (los decimales se redondean a " + PRECIO_DECIMALES + ").");
        }
        if (!entraEnDecimal(articulo.getNumeroCuenta(), CUENTA_DIGITOS, 0)) {
            throw new ArticuloValidationException("numeroCuenta", "numeroCuenta debe ser un número entero de hasta " + CUENTA_DIGITOS + " dígitos.");
        }
        validarLargo("nombre", articulo.getNombre(), NOMBRE_MAX);
        validarLargo("descripcion", articulo.getDescripcion(), DESCRIPCION_MAX);
        validarLargo("unidad", articulo.getUnidad(), UNIDAD_MAX);
    }

    private static void validarBandera(String campo, Byte valor) {
        if (valor != null && valor != 0 && valor != 1) {
            throw new ArticuloValidationException(campo, campo + " debe ser 0 o 1.");
        }
    }

    /** Nulo entra; si no, no más de {@code enteros} dígitos enteros ni {@code decimales} decimales significativos. */
    static boolean entraEnDecimal(BigDecimal valor, int enteros, int decimales) {
        if (valor == null || valor.signum() == 0) {
            return true; // el cero entra con cualquier escala; normalizarNumeros le fija la de la columna
        }
        // Dígitos enteros sobre el valor tal cual: quitar ceros a la derecha no los cambia, y con exponentes
        // enormes (100e2147483647) stripTrailingZeros desbordaría la escala
        if ((long) valor.precision() - valor.scale() > enteros) {
            return false;
        }
        return valor.stripTrailingZeros().scale() <= decimales;
    }

    /**
     * Deja precio y cuenta con la escala de su columna. Hace falta aunque ya entren: el driver escribe el valor con
     * su escala original, y un {@code 0e-1000000000} armaría un texto de mil millones de caracteres.
     * Solo después de validar: el precio se redondea como MySQL y la cuenta ya es entera.
     */
    static void normalizarNumeros(Articulo articulo) {
        if (articulo.getPrecio() != null) {
            articulo.setPrecio(redondearPrecio(articulo.getPrecio()));
        }
        if (articulo.getNumeroCuenta() != null) {
            articulo.setNumeroCuenta(articulo.getNumeroCuenta().setScale(0, RoundingMode.UNNECESSARY));
        }
    }

    /**
     * Redondeo de MySQL para decimal(16,2) (mitad hacia afuera del cero). Un valor menor que 0,001 da 0,00 directo:
     * redondear uno con escala enorme (1e-999999999) obligaría a calcular potencias de diez gigantes.
     */
    static BigDecimal redondearPrecio(BigDecimal precio) {
        if (precio.signum() == 0 || (long) precio.precision() - precio.scale() < -2) {
            return BigDecimal.ZERO.setScale(PRECIO_DECIMALES);
        }
        return precio.setScale(PRECIO_DECIMALES, RoundingMode.HALF_UP);
    }

    private static void validarLargo(String campo, String valor, int maximo) {
        if (valor != null && valor.length() > maximo) {
            throw new ArticuloValidationException(campo, campo + " admite hasta " + maximo + " caracteres.");
        }
    }

}
