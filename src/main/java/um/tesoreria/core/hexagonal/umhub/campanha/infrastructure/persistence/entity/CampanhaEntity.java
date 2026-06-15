package um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "campanha")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampanhaEntity extends Auditable {

    @Id
    private UUID campanhaId;
    private String nombre;
    private Byte activa;
    private BigDecimal valorReserva;

}
