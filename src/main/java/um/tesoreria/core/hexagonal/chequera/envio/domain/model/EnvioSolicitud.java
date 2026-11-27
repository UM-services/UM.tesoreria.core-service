package um.tesoreria.core.hexagonal.chequera.envio.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Solicitud idempotente de envío de una chequera.
 *
 * <p>{@code solicitudId} es la clave que viaja en Kafka; {@code chequeraId} (la clave de
 * {@code chequera_serie}) es única: no puede haber dos solicitudes para la misma chequera. Un
 * reenvío deliberado requiere una operación explícita (fase posterior).</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvioSolicitud {

    private UUID solicitudId;
    private Long chequeraId;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private Integer lectivoId;
    private Long jobId;
    private String origen;
    private String dedupeKey;
    private EnvioSolicitudEstado estado;
    private int intentos;
    private OffsetDateTime lockHasta;
    private OffsetDateTime enviadoAt;
    private String ultimoError;

}
