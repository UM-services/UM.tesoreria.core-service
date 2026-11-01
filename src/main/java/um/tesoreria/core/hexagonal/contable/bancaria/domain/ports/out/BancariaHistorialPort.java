package um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out;

import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;

/** Historial de escrituras, siempre dentro de la transacción que modifica la cuenta bancaria. */
public interface BancariaHistorialPort {
    void registrarAlta(Bancaria creada);
    void registrarEdicion(Bancaria anterior, Bancaria nueva);
    void registrarBaja(Bancaria anterior);
}
