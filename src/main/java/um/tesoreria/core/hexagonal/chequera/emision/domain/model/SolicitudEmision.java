package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudEmision {

    private Integer version;
    private boolean forzarNueva;
    private boolean forzarDeuda;
    private String motivoOverride;
    private String claveIdempotencia;
    /**
     * Login del operador cuando la emisión la dispara un proceso (job) y no hay identidad HTTP.
     * {@code chequera_serie.usuario_id} es un login con FK a {@code usuario.login}.
     */
    private String usuarioLogin;
}
