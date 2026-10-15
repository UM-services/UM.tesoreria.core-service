package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model;

import java.util.Collection;

/**
 * Resultado de la conformidad de lo recibido (D-REC-2): atributo de la recepción y de cada ítem.
 *
 * <p>El resultado general de la recepción se deriva del <b>peor</b> resultado por ítem
 * ({@code NO_CONFORME} &gt; {@code CON_OBSERVACIONES} &gt; {@code CONFORME}); no se toma del
 * cliente para que no contradiga al detalle.</p>
 */
public enum CompraRecepcionResultado {

    CONFORME,
    CON_OBSERVACIONES,
    NO_CONFORME;

    /** El más grave de la colección ({@link #CONFORME} cuando no hay resultados informados). */
    public static CompraRecepcionResultado masGrave(Collection<CompraRecepcionResultado> resultados) {
        CompraRecepcionResultado peor = CONFORME;
        if (resultados == null) {
            return peor;
        }
        for (CompraRecepcionResultado resultado : resultados) {
            if (resultado != null && resultado.ordinal() > peor.ordinal()) {
                peor = resultado;
            }
        }
        return peor;
    }

    /**
     * Una cantidad recibida con este resultado cubre el saldo contratado (D-REC-10): lo
     * {@code NO_CONFORME} se registra para trazabilidad pero queda pendiente de reposición.
     */
    public boolean cubreSaldo() {
        return this != NO_CONFORME;
    }

}
