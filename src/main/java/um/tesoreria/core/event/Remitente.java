package um.tesoreria.core.event;

/**
 * Clave lógica del remitente. La dirección real la resuelve el sender a partir de su propia
 * configuración, de modo que el productor no puede falsificar el {@code From}.
 */
public enum Remitente {
    CHEQUERA, RECIBO, NOTIFICACION
}
