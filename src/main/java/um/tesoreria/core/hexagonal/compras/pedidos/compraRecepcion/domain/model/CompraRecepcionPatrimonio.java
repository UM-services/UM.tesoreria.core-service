package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model;

/**
 * Intervención de Patrimonio / Inventario en la recepción (D-REC-5): sólo se registra el estado,
 * no bloquea el sello de la conformidad ni genera el alta patrimonial del bien.
 */
public enum CompraRecepcionPatrimonio {

    INTERVENIDO,
    NO_CORRESPONDE,
    PENDIENTE;

}
