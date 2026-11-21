package um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.model.Auditable;

import java.time.OffsetDateTime;

/** Secuencia autoritativa del orden contable por fecha ({@code docs/sql/V431}). */
@Getter
@Setter
@Entity
@Table(name = "contable_asiento_secuencia")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsientoSecuenciaEntity extends Auditable {

    @Id
    @Column(name = "fecha")
    private OffsetDateTime fecha;

    @Column(name = "ultimo_orden")
    @Builder.Default
    private int ultimoOrden = 0;

}
