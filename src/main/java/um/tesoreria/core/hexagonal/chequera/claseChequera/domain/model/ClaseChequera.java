package um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model;

import lombok.*;
import um.tesoreria.core.util.Jsonifier;
import um.tesoreria.core.util.Jsonifyable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaseChequera implements Jsonifyable {

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
