package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraAutoridadUsuarioResponse {

    private Integer usuarioId;
    private List<Long> autoridadPerfilIds;

}
