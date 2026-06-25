package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionGuarani {

    private Integer ubicacion;
    private String nombre;
    private Integer ubicacionTipo;
    private Integer localidad;
    private String calle;
    private String numero;
    private String codigoPostal;
    private String telefono;
    private String fax;
    private String email;
    private String institucionAraucano;
    private BigDecimal latitud;
    private BigDecimal longitud;

    public String jsonify() {
        return um.tesoreria.core.util.Jsonifier.builder(this).build();
    }

}
