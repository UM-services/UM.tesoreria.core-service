package um.tesoreria.core.hexagonal.documento.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Documento {

    private Integer documentoId;
    private String nombre;

}
