package um.tesoreria.core.hexagonal.track.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackResponse {
    private Long trackId;
    @Builder.Default
    private String descripcion = "";
}
