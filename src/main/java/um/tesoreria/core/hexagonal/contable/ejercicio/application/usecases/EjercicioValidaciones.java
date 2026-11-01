package um.tesoreria.core.hexagonal.contable.ejercicio.application.usecases;

import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioSolicitudInvalidaException;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;

final class EjercicioValidaciones {

    private EjercicioValidaciones() {
    }

    /** orden_contable_* es smallint: fuera de 0..32767 la base rechaza el valor con un error sin cuerpo. */
    static void validarOrden(String campo, Integer orden) {
        if (orden != null && (orden < 0 || orden > Short.MAX_VALUE)) {
            throw new EjercicioSolicitudInvalidaException(campo + " debe estar entre 0 y " + Short.MAX_VALUE);
        }
    }

    static final int LARGO_NOMBRE = 128;

    /** Las tres validaciones de campos acotados por la columna (bloqueado, órdenes smallint, nombre varchar(128)). */
    static void validar(Ejercicio ejercicio) {
        validarBloqueado(ejercicio.getBloqueado());
        validarOrden("ordenContableResultado", ejercicio.getOrdenContableResultado());
        validarOrden("ordenContableBienesUso", ejercicio.getOrdenContableBienesUso());
        if (ejercicio.getNombre() != null && ejercicio.getNombre().length() > LARGO_NOMBRE) {
            throw new EjercicioSolicitudInvalidaException("nombre no puede superar " + LARGO_NOMBRE + " caracteres");
        }
    }

    /** Un nombre en blanco o un id menor o igual a cero significan «no definido» para el cliente VB6. */
    static Ejercicio sinSentinelas(Ejercicio ejercicio) {
        boolean sinNombre = ejercicio.getNombre() != null && ejercicio.getNombre().isBlank();
        boolean sinId = ejercicio.getEjercicioId() != null && ejercicio.getEjercicioId() <= 0;
        if (!sinNombre && !sinId) {
            return ejercicio;
        }
        return ejercicio.toBuilder()
                .nombre(sinNombre ? null : ejercicio.getNombre())
                .ejercicioId(sinId ? null : ejercicio.getEjercicioId())
                .build();
    }

    static <T> T valor(T enviado, T porDefecto) {
        return enviado == null ? porDefecto : enviado;
    }

    static void validarBloqueado(Byte bloqueado) {
        if (bloqueado != null && bloqueado != 0 && bloqueado != 1) {
            throw new EjercicioSolicitudInvalidaException("bloqueado debe ser 0 o 1");
        }
    }
}
