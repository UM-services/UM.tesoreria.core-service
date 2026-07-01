package um.tesoreria.core.hexagonal.chequera.producto.domain.model;

import lombok.*;
import um.tesoreria.core.util.Jsonifier;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    private Integer productoId;
    private String nombre;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }
}
