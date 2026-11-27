package um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.envio.domain.model.DeudaEnvio;

import java.math.BigDecimal;

/**
 * Puerta de salida hacia el resto del sistema (chequeras, personas y correo).
 *
 * <p>Anticorrupción del slice: la capa de aplicación no conoce servicios de otros slices ni legacy;
 * el adaptador de infraestructura compone {@code DomicilioService}, {@code ChequeraService},
 * {@code PersonaService} y {@code MailChequeraService}.</p>
 */
public interface EnvioChequeraGateway {

    /** Captura/actualiza los correos del domicilio desde la fuente externa de la facultad. */
    Integer capturarCorreos(BigDecimal personaId, Integer documentoId);

    /** Extiende los débitos hasta la última cuota (respeta bajas y vencimientos futuros). */
    void extenderDebito(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);

    /** Deuda de la persona por chequera (sólo lectivo y cantidad de cuotas impagas). */
    DeudaEnvio deuda(BigDecimal personaId, Integer documentoId);

    /** Escribe sólo la columna {@code retenida} de la serie (nunca el registro completo). */
    void marcarRetenida(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, boolean retenida);

    /** Publica el envío de la chequera (bulk + refresco de códigos de barras). Devuelve la respuesta. */
    String enviarChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                          Integer alternativaId, boolean codigoBarras, Long jobId);

    /** Publica la notificación de deuda. Devuelve la respuesta. */
    String notificarDeuda(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);
}
