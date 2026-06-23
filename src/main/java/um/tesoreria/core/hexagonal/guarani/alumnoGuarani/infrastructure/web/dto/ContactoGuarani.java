package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactoGuarani {

    private Long personaContacto;
    private Long persona;
    private String contactoTipo;
    private String otrosContactos;
    private String email;
    private String telefonoCodigoArea;
    private String telefonoNumero;

}
