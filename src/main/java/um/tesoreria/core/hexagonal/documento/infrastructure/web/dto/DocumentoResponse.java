package um.tesoreria.core.hexagonal.documento.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoResponse {

    private Integer documentoId;
    private String nombre;

}
