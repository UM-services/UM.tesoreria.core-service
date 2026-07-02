package um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model;

import lombok.*;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;
import um.tesoreria.core.hexagonal.geografica.domain.model.Geografica;
import um.tesoreria.core.util.Jsonifier;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoChequera {
    private Integer tipoChequeraId;
    private String nombre;
    private String prefijo;
    private Integer geograficaId;
    private Integer claseChequeraId;
    private Byte imprimir;
    private Byte contado;
    private Byte multiple;
    private String emailCopia;
    private Geografica geografica;
    private ClaseChequera claseChequera;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }
}
