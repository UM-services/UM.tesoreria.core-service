package um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.entity.TipoChequeraEntity;
import um.tesoreria.core.hexagonal.lectivo.infrastructure.persistence.entity.LectivoEntity;
import um.tesoreria.core.model.Auditable;

@Getter
@Setter
@Entity
@Table(name = "guarani_propuesta_tipo_chequera", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"propuesta_guarani", "lectivo_id"})
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuaraniPropuestaTipoChequeraEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guarani_propuesta_tipo_chequera_id")
    private Integer guaraniPropuestaTipoChequeraId;

    @Column(name = "propuesta_guarani")
    private Integer propuestaGuarani;

    @Column(name = "lectivo_id")
    private Integer lectivoId;

    @Column(name = "tipo_chequera_id")
    private Integer tipoChequeraId;

    @OneToOne(optional = true)
    @JoinColumn(name = "lectivo_id", insertable = false, updatable = false)
    private LectivoEntity lectivo;

    @OneToOne(optional = true)
    @JoinColumn(name = "tipo_chequera_id", insertable = false, updatable = false)
    private TipoChequeraEntity tipoChequera;

}
