package um.tesoreria.core.hexagonal.track.domain.model;

import lombok.*;
import um.tesoreria.core.util.Jsonifyable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Track implements Jsonifyable {
    private Long trackId;
    @Builder.Default
    private String descripcion = "";
}
