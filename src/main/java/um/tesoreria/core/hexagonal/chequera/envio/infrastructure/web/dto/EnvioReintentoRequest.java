package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvioReintentoRequest {

    /** true = sólo ERROR/ERROR_SMTP; false = también omitidas y sin correo. */
    @Builder.Default
    private boolean soloFallidas = true;
}
