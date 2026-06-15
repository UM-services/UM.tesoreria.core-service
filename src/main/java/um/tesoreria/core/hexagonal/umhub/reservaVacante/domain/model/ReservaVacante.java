package um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model;

import lombok.*;
import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.persona.domain.model.Persona;
import um.tesoreria.core.util.Jsonifier;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaVacante {

    private UUID reservaVacanteId;
    private Long personaUniqueId;
    private UUID campanhaId;
    private String estado;
    private Persona persona;
    private Domicilio domicilio;
    private LocalDateTime created;
    private LocalDateTime updated;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }
}
