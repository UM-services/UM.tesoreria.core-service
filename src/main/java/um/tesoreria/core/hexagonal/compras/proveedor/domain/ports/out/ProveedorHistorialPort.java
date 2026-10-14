package um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;

/**
 * Registra las escrituras de proveedor en el historial transaccional (#404). Debe invocarse dentro
 * de la transacción de la escritura: si esta se revierte, el evento tampoco queda.
 */
public interface ProveedorHistorialPort {

    void registrarAlta(Proveedor creado);

    void registrarEdicion(Proveedor anterior, Proveedor nuevo);

    void registrarBaja(Proveedor anterior);
}