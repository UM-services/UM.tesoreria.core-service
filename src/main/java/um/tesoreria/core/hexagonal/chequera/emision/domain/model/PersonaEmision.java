package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaEmision {

    private BigDecimal personaId;
    private Integer documentoId;
    private String apellido;
    private String nombre;

    public String nombreCompleto() {
        return (apellido == null ? "" : apellido) + ", " + (nombre == null ? "" : nombre);
    }
}
