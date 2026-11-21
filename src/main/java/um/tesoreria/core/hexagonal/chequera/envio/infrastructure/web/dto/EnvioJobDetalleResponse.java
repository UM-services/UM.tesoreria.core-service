package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record EnvioJobDetalleResponse(EnvioJobResponse job, List<EnvioJobTareaResponse> tareas) {
}
