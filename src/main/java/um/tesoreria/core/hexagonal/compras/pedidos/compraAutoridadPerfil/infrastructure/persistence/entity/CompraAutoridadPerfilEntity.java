package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.model.Auditable;

@Getter
@Setter
@Entity
@Table(name = "compra_autoridad_perfil")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraAutoridadPerfilEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "autoridad_perfil_id")
    private Long autoridadPerfilId;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    /** Múltiplo de la referencia; nulo = sin límite. */
    @Column(name = "multiplico")
    private Integer multiplico;

    @Builder.Default
    @Column(name = "activo", nullable = false)
    private Byte activo = 1;

}
