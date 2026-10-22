package um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.in;

import java.util.UUID;

/**
 * Claim de la notificación de pago de una reserva de vacante.
 * <p>
 * {@code umhub-service} no tiene base de datos: la marca de "ya notificado" vive acá, en el
 * servicio dueño de {@code reserva_vacante}. El primero que reclama es el único que notifica.
 */
public interface ReclamarNotificacionPagoUseCase {

    /**
     * @return {@code true} si esta instancia ganó el claim y debe notificar; {@code false} si la
     * notificación ya había sido reclamada por otra entrega del evento.
     */
    boolean reclamar(UUID reservaVacanteId);

    /**
     * Devuelve la marca a 0 para que el reintento de Kafka pueda volver a reclamarla. Se usa cuando
     * el envío a n8n falló.
     */
    void liberar(UUID reservaVacanteId);

}
