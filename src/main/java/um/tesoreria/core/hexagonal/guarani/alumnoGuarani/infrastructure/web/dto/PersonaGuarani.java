package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonaGuarani {

    private Long personaId;
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

}
