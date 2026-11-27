package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto;

public record EnvioSolicitudClaimResponse(String solicitudId,
                                          boolean puedeEnviar,
                                          String estado,
                                          int intentos,
                                          String motivo) {
}
