package um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model;

import lombok.*;
import um.tesoreria.core.util.Jsonifier;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaseChequera {

    private Integer claseChequeraId;
    private String nombre;
    private Byte preuniversitario;
    private Byte grado;
    private Byte posgrado;
    private Byte curso;
    private Byte secundario;
    private Byte titulo;
    private Byte tramite;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }

}
