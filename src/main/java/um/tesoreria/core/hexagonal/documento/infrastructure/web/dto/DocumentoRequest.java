package um.tesoreria.core.hexagonal.documento.infrastructure.web.dto;

import lombok.Data;

@Data
public class DocumentoRequest {

    private Integer documentoId;
    private String nombre;

}
