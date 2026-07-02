package um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.web.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArancelTipoResponse {
    private Integer arancelTipoId;
    private String descripcion;
    private Byte medioArancel;
    private Integer arancelTipoIdCompleto;
    private Byte habilitado;
}
