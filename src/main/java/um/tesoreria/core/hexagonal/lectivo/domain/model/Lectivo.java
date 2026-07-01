package um.tesoreria.core.hexagonal.lectivo.domain.model;

import lombok.*;
import um.tesoreria.core.util.Jsonifier;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lectivo {
    private Integer lectivoId;
    private String nombre;
    private OffsetDateTime fechaInicio;
    private OffsetDateTime fechaFinal;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }
}
