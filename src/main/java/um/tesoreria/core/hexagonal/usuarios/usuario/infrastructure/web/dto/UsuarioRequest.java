package um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioRequest {
    @NotNull
    private String login;
    @NotNull
    private String password;
    @NotNull
    private String nombre;
    private Integer dependenciaId;
    @NotNull
    private Integer geograficaId;
    @NotNull
    private Byte imprimeChequera;
    @NotNull
    private Byte numeroOpManual;
    @NotNull
    private Byte habilitaOpEliminacion;
    @NotNull
    private Byte eliminaChequera;
    @NotNull
    @JsonAlias("modificaCHequera")
    private Byte modificaChequera;
    private String googleMail;
    @NotNull
    private Byte activo;
    private Byte administrador;
    private Byte usuarioExterno;
}
