package um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.web.dto;

import lombok.Data;

@Data
public class ArancelTipoRequest {
    private Integer arancelTipoId;
    private String descripcion;
    private Byte medioArancel;
    private Integer arancelTipoIdCompleto;
    private Byte habilitado;
}
