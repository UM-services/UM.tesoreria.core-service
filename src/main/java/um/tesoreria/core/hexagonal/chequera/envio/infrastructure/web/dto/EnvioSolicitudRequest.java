package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto;

public record EnvioSolicitudRequest(Integer facultadId,
                                    Integer tipoChequeraId,
                                    Long chequeraSerieId,
                                    Long jobId,
                                    String origen) {
}
