package um.tesoreria.core.hexagonal.chequera.producto.infrastructure.web.dto;

import lombok.Data;

@Data
public class ProductoRequest {
    private Integer productoId;
    private String nombre;
}
