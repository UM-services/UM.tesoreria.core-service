package um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.entity.PersonaEntity;
import um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.persistence.entity.CampanhaEntity;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "reserva_vacante")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaVacanteEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reservaVacanteId;

    private Long personaUniqueId;
    private UUID campanhaId;
    private String estado;
    private BigDecimal importe;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime vencimiento;

    @OneToOne(optional = true)
    @JoinColumn(name = "personaUniqueId", referencedColumnName = "clave", insertable = false, updatable = false)
    private PersonaEntity persona;

    @OneToOne(optional = true)
    @JoinColumn(name = "campanhaId", insertable = false, updatable = false)
    private CampanhaEntity campanha;

}
