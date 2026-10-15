package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraReferenciaResponse {

    private Integer ejercicioId;
    private BigDecimal importe;
    private LocalDateTime created;
    private LocalDateTime updated;

}
