package um.tesoreria.core.hexagonal.usuario.infrastructure.web.dto;

import lombok.Data;

@Data
public class UsuarioRequest {
    private String login;
    private String password;
    private String nombre;
    private Integer geograficaId;
    private Byte imprimeChequera;
    private Byte numeroOpManual;
    private Byte habilitaOpEliminacion;
    private Byte eliminaChequera;
    private Byte modificaChequera;
    private String googleMail;
    private Byte activo;
}
