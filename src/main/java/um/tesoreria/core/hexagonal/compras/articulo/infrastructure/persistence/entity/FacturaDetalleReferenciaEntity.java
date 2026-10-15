package um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

/**
 * Solo lectura: líneas de factura de proveedor que usan un artículo. La PK de la tabla es ({@code FaD_MvP_ID},
 * {@code FaD_Orden}); se mapea {@code FaD_ID} (autoincremental, no nulo; dev, 2026-10-13) porque es el id con el que
 * otras entidades ya mapean la tabla y Hibernate exige el mismo en todas.
 */
@Getter
@Entity
@Immutable
@Table(name = "movprov_detallefactura")
@NoArgsConstructor
public class FacturaDetalleReferenciaEntity {

    @Id
    @Column(name = "FaD_ID")
    private Long facturaDetalleId;

    @Column(name = "FaD_Art_ID")
    private Long articuloId;

}
