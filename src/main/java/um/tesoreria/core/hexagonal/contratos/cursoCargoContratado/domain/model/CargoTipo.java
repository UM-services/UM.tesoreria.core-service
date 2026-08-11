package um.tesoreria.core.hexagonal.contratos.cursoCargoContratado.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CargoTipo {

    private Integer cargoTipoId;
    private Byte aCargo;
    private String nombre;
    private Integer precedencia;

}