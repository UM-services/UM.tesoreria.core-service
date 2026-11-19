package um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out;

/**
 * Reserva el próximo número de chequera (chs_id) de una facultad y tipo, con bloqueo
 * pesimista sobre la tabla de control.
 */
public interface NumeradorChequeraPort {

    /**
     * @return número de chequera libre y registrado en {@code chequera_serie_control}.
     */
    Long siguienteNumero(Integer facultadId, Integer tipoChequeraId);
}
