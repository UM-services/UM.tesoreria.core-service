package um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in;

import java.time.OffsetDateTime;

/** Reserva el próximo orden contable de una fecha, de forma serializada entre procesos. */
public interface ReservarOrdenContableUseCase {

    /**
     * @param fecha fecha contable (se normaliza a 00:00 UTC)
     * @return el orden contable reservado, exclusivo para quien lo pidió
     */
    Integer reservar(OffsetDateTime fecha);

    /**
     * Registra un orden asignado manualmente para que la secuencia no vuelva a entregarlo.
     * Debe llamarse dentro de la transacción que crea el asiento.
     */
    void registrarUso(OffsetDateTime fecha, Integer orden);

}
