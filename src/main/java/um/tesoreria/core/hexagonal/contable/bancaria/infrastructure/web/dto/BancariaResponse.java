package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.web.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Las mismas claves planas que el listado GET /bancaria/, sin el objeto cuenta anidado. */
@Data
public class BancariaResponse {
    private Long bancariaId;
    private String banco;
    private String numero;
    private String cbu;
    private String titular;
    private String cuit;
    private BigDecimal numeroCuenta;
    private Integer proveedorId;
    private LocalDateTime created;
    private LocalDateTime updated;
}
