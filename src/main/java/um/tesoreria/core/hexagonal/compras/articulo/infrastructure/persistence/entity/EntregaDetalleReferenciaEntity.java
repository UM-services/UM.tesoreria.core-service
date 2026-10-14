package um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

/**
 * Solo lectura: líneas de entrega que usan un artículo. PK {@code NeD_ID} (dev, 2026-10-13). La tabla se nombra
 * {@code EntregaDetalle} (la estrategia de nombres la lleva a {@code entrega_detalle}) porque así la nombra otra entidad
 * de la misma tabla y Hibernate exige un único nombre lógico por tabla.
 */
@Getter
@Entity
@Immutable
@Table(name = "EntregaDetalle")
@NoArgsConstructor
public class EntregaDetalleReferenciaEntity {

    @Id
    @Column(name = "NeD_ID")
    private Long entregaDetalleId;

    @Column(name = "NeD_Art_ID")
    private Long articuloId;

}
