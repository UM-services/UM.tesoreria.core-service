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
}
