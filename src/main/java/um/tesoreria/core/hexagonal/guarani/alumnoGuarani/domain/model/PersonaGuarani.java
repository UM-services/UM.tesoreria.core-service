package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaGuarani {

    private Long persona;
    private String apellido;
    private String nombres;
    private String apellidoElegido;
    private String nombresElegido;
    private String sexo;
    private Integer identidadGenero;
    private String identidadGeneroOtro;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;
    private String localidadNacimiento;
    private Integer nacionalidad;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaIngresoPais;
    private String paisOrigen;
    private Long documentoPrincipal;
    private DocumentoPrincipalGuarani documentoPrincipalRel;
    private List<ContactoGuarani> contactos;
    private String usuario;
    private String clave;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaVencimientoClave;
    private String autenticacion;
    private Integer bloqueado;
    private String token;
    private String tokenAlta;
    private String emailTemporal;
    private Integer emailValido;
    private String idImagen;
    private String tipoUsuarioInicial;
    private String pertenecePuebloOriginario;
    private String puebloOriginario;
    private String puebloOriginarioOtro;
    private String araiIdentificadorSso;
    private String araiUuid;

    public String jsonify() {
        return um.tesoreria.core.util.Jsonifier.builder(this).build();
    }

}
