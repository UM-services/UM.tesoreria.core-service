package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraAutoridadPerfilResponse {

    private Long autoridadPerfilId;
    private String nombre;
    private Integer multiplico;
    /** Derivado: true cuando multiplico es nulo (autoriza sin límite). */
    private Boolean ilimitado;
    private Byte activo;
    private LocalDateTime created;
    private LocalDateTime updated;

}
