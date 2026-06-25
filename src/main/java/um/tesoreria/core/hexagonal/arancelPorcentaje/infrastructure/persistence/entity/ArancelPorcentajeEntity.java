package um.tesoreria.core.hexagonal.arancelPorcentaje.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "arancel_porcentaje", uniqueConstraints = {@UniqueConstraint(columnNames = {"aranceltipoId", "productoId"})})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArancelPorcentajeEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long arancelporcentajeId;

    private Integer aranceltipoId;
    private Integer productoId;

    @Builder.Default
    private BigDecimal porcentaje = BigDecimal.ZERO;

}
