package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraReferenciaRequest {

    @NotNull
    private BigDecimal importe;

}
