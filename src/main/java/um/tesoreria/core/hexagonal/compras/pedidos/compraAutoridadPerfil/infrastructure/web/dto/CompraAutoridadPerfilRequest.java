package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraAutoridadPerfilRequest {

    @NotBlank
    private String nombre;

    /** Múltiplo de la referencia; nulo = sin límite. */
    private Integer multiplico;

    private Byte activo;

}
