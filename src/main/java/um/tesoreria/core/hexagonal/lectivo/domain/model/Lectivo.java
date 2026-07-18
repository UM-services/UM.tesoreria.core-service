package um.tesoreria.core.hexagonal.lectivo.domain.model;

import lombok.*;
import um.tesoreria.core.util.Jsonifier;
import um.tesoreria.core.util.Jsonifyable;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lectivo implements Jsonifyable {
    private Integer lectivoId;
    private String nombre;
    private OffsetDateTime fechaInicio;
    private OffsetDateTime fechaFinal;

}
