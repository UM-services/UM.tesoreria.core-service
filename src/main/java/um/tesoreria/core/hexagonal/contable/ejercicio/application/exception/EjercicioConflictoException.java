package um.tesoreria.core.hexagonal.contable.ejercicio.application.exception;

/** Rechazo de una regla de ejercicios; la web lo responde con 409 y cuerpo JSON con el código. */
public class EjercicioConflictoException extends RuntimeException {

    public enum Codigo {
        SALTO, DUPLICADO, LIMITE_ALTA, NO_ES_ULTIMO, NO_FUTURO, EN_USO, CAMPO_INMUTABLE, CONCURRENCIA
    }

    private final Codigo codigo;

    public EjercicioConflictoException(Codigo codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public EjercicioConflictoException(Codigo codigo, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigo = codigo;
    }

    public Codigo getCodigo() {
        return codigo;
    }
}
