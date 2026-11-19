package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermisosEmision {

    private boolean puedeEmitir;
    private boolean puedeImprimir;
    private boolean puedeModificar;
    private boolean puedeForzarDeuda;
}
