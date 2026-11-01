package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.adapter;

import org.hibernate.exception.ConstraintViolationException;

/**
 * Solo una clave foránea rota es un conflicto que el cliente puede entender y reintentar. Hibernate ya la clasifica
 * por dialecto (también en Postgres), sin leer códigos de error de MySQL. Las demás violaciones (NOT NULL,
 * duplicados, largos) son defectos nuestros y siguen como error interno.
 */
final class ViolacionDeIntegridad {

    private ViolacionDeIntegridad() {
    }

    static boolean esClaveForanea(Throwable error) {
        for (Throwable causa = error; causa != null; causa = causa.getCause() == causa ? null : causa.getCause()) {
            if (causa instanceof ConstraintViolationException cv
                    && cv.getKind() == ConstraintViolationException.ConstraintKind.FOREIGN_KEY) {
                return true;
            }
        }
        return false;
    }
}
