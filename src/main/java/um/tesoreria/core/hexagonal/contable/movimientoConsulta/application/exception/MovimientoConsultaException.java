package um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.exception;

import lombok.Getter;

/** Rechazo de negocio de una consulta de movimientos contables; {@code codigo} identifica la regla incumplida. */
@Getter
public class MovimientoConsultaException extends RuntimeException {

    public enum Codigo {
        FECHAS_REQUERIDAS,
        FECHA_FUERA_DE_RANGO,
        CUERPO_INVALIDO,
        DESDE_POSTERIOR,
        RANGO_DEMASIADO_LARGO,
        DEMASIADOS_MOVIMIENTOS
    }

    private final Codigo codigo;

    public MovimientoConsultaException(Codigo codigo, String message) {
        super(message);
        this.codigo = codigo;
    }
}
