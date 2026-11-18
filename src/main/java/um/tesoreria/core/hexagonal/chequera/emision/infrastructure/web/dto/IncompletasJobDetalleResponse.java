package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record IncompletasJobDetalleResponse(IncompletasJobResponse job,
                                            List<IncompletasJobTareaResponse> tareas) {
}
