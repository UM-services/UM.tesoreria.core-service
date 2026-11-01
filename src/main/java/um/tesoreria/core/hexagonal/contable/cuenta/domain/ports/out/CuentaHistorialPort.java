package um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out;

import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;

/** Historial de escrituras, siempre dentro de la transacción que modifica la cuenta. */
public interface CuentaHistorialPort {
    void registrarAlta(Cuenta creada);
    void registrarEdicion(Cuenta anterior, Cuenta nueva);
    void registrarBaja(Cuenta anterior);
}
