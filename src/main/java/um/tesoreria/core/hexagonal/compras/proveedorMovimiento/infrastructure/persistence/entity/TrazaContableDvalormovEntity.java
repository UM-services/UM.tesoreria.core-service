package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

/** Rama {@code vw_trazaContableDvalormov} de la traza contable (sólo lectura). */
@Entity
@Immutable
@Table(name = "`vw_trazaContableDvalormov`")
public class TrazaContableDvalormovEntity extends TrazaContableRamaEntity {
}
