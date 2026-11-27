package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto;

import java.time.OffsetDateTime;

public record EnvioSolicitudResponse(String solicitudId,
                                     Long chequeraId,
                                     Integer facultadId,
                                     Integer tipoChequeraId,
                                     Long chequeraSerieId,
                                     Integer lectivoId,
                                     Long jobId,
                                     String estado,
                                     int intentos,
                                     String ultimoError,
                                     OffsetDateTime enviadoAt) {
}
