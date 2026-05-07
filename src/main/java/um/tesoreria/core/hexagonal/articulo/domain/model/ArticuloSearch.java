package um.tesoreria.core.hexagonal.articulo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloSearch {
    private Long articuloId;
    private String nombre;
    private String descripcion;
    private String unidad;
    private BigDecimal precio;
    private Byte inventariable;
    private Long stockMinimo;
    private BigDecimal cuenta;
    private String tipo;
    private Byte directo;
    private Byte habilitado;
    private String search;
    private java.time.OffsetDateTime fechaAuditoria;
    private String usuarioAuditoria;
}
