package um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.model;

import lombok.*;
import um.tesoreria.core.util.Jsonifier;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArancelTipo {

    private Integer arancelTipoId;
    private String descripcion;
    private Byte medioArancel;
    private Integer arancelTipoIdCompleto;
    private Byte habilitado;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }

}
