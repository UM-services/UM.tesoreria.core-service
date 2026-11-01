package um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out;

import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;

/** Historial de escrituras, siempre dentro de la transacción que modifica el ejercicio. */
public interface EjercicioHistorialPort {
    void registrarAlta(Ejercicio creado);
    void registrarEdicion(Ejercicio anterior, Ejercicio nuevo);
    void registrarBaja(Ejercicio anterior);
}
