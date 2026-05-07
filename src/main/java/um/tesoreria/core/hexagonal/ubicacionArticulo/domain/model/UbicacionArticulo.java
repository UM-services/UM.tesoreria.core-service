package um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.ubicacion.domain.model.Ubicacion;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionArticulo {
    private Long ubicacionArticuloId;
    private Integer ubicacionId;
    private Long articuloId;
    private BigDecimal numeroCuenta;
    private Ubicacion ubicacion;
    private Articulo articulo;
    private Cuenta cuenta;
}
