package um.tesoreria.core.hexagonal.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAuth {
    private Long userId;
    private String login;
    private String password;
    private String nombre;
    private Integer geograficaId;
    private Byte imprimeChequera;
    private Byte numeroOpManual;
    private Byte habilitaOpEliminacion;
    private Byte eliminaChequera;
    private OffsetDateTime lastLog;
    private String googleMail;
    private Byte activo;

    public boolean isActivo() {
        return this.activo != null && this.activo == 1;
    }
}
