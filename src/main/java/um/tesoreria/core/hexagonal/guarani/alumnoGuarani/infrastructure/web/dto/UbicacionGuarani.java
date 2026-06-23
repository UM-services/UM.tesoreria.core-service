package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UbicacionGuarani {

    private Integer ubicacionId;
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

}
