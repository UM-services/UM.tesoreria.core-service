package um.tesoreria.core.hexagonal.cuenta.infrastructure.web.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
@Data
public class CuentaSearchResponse {
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
