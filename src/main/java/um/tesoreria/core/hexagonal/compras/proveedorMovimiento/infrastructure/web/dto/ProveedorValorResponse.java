package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorValorResponse {

    private Long valorMovimientoId;
    private String concepto;
    private Long numero;
    private OffsetDateTime fechaEmision;
    private BigDecimal importe;
    private String nombreTitular;
    private String bancaria;
}
