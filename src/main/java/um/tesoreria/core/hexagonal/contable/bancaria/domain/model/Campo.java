package um.tesoreria.core.hexagonal.contable.bancaria.domain.model;

/**
 * Un campo de un pedido de escritura que distingue «no vino» de «vino en null»: en la edición lo primero conserva
 * lo guardado y lo segundo lo vacía.
 */
public record Campo<T>(boolean presente, T valor) {

    private static final Campo<?> AUSENTE = new Campo<>(false, null);

    @SuppressWarnings("unchecked")
    public static <T> Campo<T> ausente() {
        return (Campo<T>) AUSENTE;
    }

    public static <T> Campo<T> de(T valor) {
        return new Campo<>(true, valor);
    }

    public T valorOSi(T porDefecto) {
        return presente ? valor : porDefecto;
    }
}
