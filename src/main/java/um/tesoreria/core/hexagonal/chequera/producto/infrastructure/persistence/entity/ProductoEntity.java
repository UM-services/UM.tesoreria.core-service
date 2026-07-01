package um.tesoreria.core.hexagonal.chequera.producto.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import um.tesoreria.core.model.Auditable;
import um.tesoreria.core.util.Jsonifier;

@Getter
@Setter
@Entity
@Table(name = "producto")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoEntity extends Auditable {

    @Id
    @Column(name = "pro_id")
    private Integer productoId;

    @Builder.Default
    @Column(name = "pro_nombre")
    private String nombre = "";

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }

}
