package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.web.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LectivoTotalImputacionRequest {
    private Long lectivoTotalImputacionId;
    private Integer facultadId;
    private Integer lectivoId;
    private Integer tipoChequeraId;
    private Integer productoId;
    private BigDecimal numeroCuenta;
}
