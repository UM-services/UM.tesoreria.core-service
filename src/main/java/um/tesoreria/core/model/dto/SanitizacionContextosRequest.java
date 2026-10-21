package um.tesoreria.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Pedido de sanitizacion de contextos activos duplicados.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SanitizacionContextosRequest {

    @Builder.Default
    private Boolean dryRun = Boolean.TRUE;

}
