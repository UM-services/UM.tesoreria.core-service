package um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaseChequeraResponse {

    private Integer claseChequeraId;
    private String nombre;
    private Byte preuniversitario;
    private Byte grado;
    private Byte posgrado;
    private Byte curso;
    private Byte secundario;
    private Byte titulo;
    private Byte tramite;

}
