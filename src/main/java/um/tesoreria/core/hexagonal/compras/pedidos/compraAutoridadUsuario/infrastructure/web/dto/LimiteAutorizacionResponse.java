package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.web.dto;

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
public class LimiteAutorizacionResponse {

    private Integer usuarioId;
    private Integer ejercicioId;
    private Integer multiplico;
    private BigDecimal referencia;
    private BigDecimal limite;
    private Boolean ilimitado;
    private Boolean tieneAutoridad;

}
