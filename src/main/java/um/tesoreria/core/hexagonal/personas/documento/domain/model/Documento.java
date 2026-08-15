package um.tesoreria.core.hexagonal.personas.documento.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Documento {

    private Integer documentoId;
    private String nombre;
    private Integer guaraniTipoDocumento;

}
