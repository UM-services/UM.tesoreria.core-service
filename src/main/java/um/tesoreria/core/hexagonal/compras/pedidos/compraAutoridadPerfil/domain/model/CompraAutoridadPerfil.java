package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Perfil de autoridad de compras: un nivel de autorización expresado como múltiplo de la
 * referencia del ejercicio ({@code compra_referencia}). {@code multiplico = null} significa
 * <b>sin límite</b>.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraAutoridadPerfil {

    private Long autoridadPerfilId;
    private String nombre;
    private Integer multiplico;
    @Builder.Default
    private Byte activo = 1;
    private LocalDateTime created;
    private LocalDateTime updated;
}
