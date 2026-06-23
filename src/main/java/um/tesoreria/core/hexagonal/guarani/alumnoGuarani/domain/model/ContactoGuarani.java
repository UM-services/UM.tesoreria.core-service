package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactoGuarani {

    private Long personaContacto;
    private Long persona;
    private String contactoTipo;
    private String otrosContactos;
    private String email;
    private String telefonoCodigoArea;
    private String telefonoNumero;

    public String jsonify() {
        return um.tesoreria.core.util.Jsonifier.builder(this).build();
    }

}
