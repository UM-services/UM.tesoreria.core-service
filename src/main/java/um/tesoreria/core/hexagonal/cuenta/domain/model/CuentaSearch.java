package um.tesoreria.core.hexagonal.cuenta.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaSearch {
    private BigDecimal numeroCuenta;
    private String nombre;
    private Byte integradora;
    private Integer grado;
    private BigDecimal grado1;
    private BigDecimal grado2;
    private BigDecimal grado3;
    private BigDecimal grado4;
    private Integer geograficaId;
    private OffsetDateTime fechaBloqueo;
    private Long cuentaContableId;
    private Byte visible;
    private String search;
}
