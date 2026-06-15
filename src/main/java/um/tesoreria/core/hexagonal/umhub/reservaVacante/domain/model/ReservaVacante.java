package um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.model.Campanha;
import um.tesoreria.core.util.Jsonifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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
    private BigDecimal importe;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime vencimiento;
    private Campanha campanha;
    private Persona persona;
    private Domicilio domicilio;
    private LocalDateTime created;
    private LocalDateTime updated;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }
}
