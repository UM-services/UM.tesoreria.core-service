package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "guarani_beneficio", uniqueConstraints = {@UniqueConstraint(columnNames = {"requisito"})})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuaraniBeneficioEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer guaraniBeneficioId;

    private Integer requisito;

    @Builder.Default
    private BigDecimal porcentajeBeneficio = BigDecimal.ZERO;

}
