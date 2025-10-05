package um.tesoreria.core.hexagonal.cursoCargoContratado.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.kotlin.model.Auditable;
import um.tesoreria.core.kotlin.model.view.ContratadoPersona;
import um.tesoreria.core.util.Jsonifier;

import java.math.BigDecimal;

@Data
@Entity
@Table(
        name = "curso_cargo_contratado",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"cursoId", "anho", "mes", "contratoId"})
        }
)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CursoCargoContratadoEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cursoCargoContratadoId = null;

    private Long cursoId = null;
    private Integer anho = 0;
    private Integer mes = 0;
    private Long contratoId = null;
    private BigDecimal personaId = null;
    private Integer documentoId = null;
    private Integer cargoTipoId = null;
    private BigDecimal horasSemanales = BigDecimal.ZERO;
    private BigDecimal horasTotales = BigDecimal.ZERO;
    private Integer designacionTipoId = null;
    private Integer categoriaId = null;
    private Long cursoCargoNovedadId = null;
    private Byte acreditado = 0;

    @OneToOne(optional = true)
    @JoinColumns({
            @JoinColumn(name = "personaId", referencedColumnName = "personaId", insertable = false, updatable = false),
            @JoinColumn(name = "documentoId", referencedColumnName = "documentoId", insertable = false, updatable = false)
    })
    private ContratadoPersona contratadoPersona = null;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }

    public String getPeriodo() {
        return this.anho + "." + this.mes;
    }

}