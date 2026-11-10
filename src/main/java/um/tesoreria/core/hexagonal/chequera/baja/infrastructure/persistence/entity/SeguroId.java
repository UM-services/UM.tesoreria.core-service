package um.tesoreria.core.hexagonal.chequera.baja.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeguroId implements Serializable {

    private Integer documentoId;
    private BigDecimal personaId;
    private Integer lectivoId;

}
