package um.tesoreria.core.hexagonal.track.infrastructure.web.dto;

import lombok.Data;

@Data
public class TrackRequest {
    private Long trackId;
    private String descripcion;
}
