package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.persistence.entity;

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
@Table(name = "compra_cotizacion_tramo")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraCotizacionTramoEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cotizacion_tramo_id")
    private Long cotizacionTramoId;

    @Column(name = "nombre", length = 80)
    private String nombre;

    /** Tope en múltiplos de la referencia; nulo = tramo sin tope (último). */
    @Column(name = "tope_multiplico")
    private Integer topeMultiplico;

    @Column(name = "cantidad_minima", nullable = false)
    private Integer cantidadMinima;

    @Builder.Default
    @Column(name = "activo", nullable = false)
    private Byte activo = 1;

}
