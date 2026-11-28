package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record BorradorListadoResponse(List<BorradorListadoItemResponse> items,
                                      long total,
                                      int page,
                                      int size) {
}
