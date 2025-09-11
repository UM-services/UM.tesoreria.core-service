package um.tesoreria.core.model.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultadSedeChequeraDto {

    private Integer facultadId;
    private String facultad;
    private Integer geograficaId;
    private String geografica;
    private Long cantidad;

}
