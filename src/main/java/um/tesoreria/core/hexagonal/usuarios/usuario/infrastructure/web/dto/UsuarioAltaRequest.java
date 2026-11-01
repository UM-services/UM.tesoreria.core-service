package um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Alta de usuario desde el módulo Administrador. El login ya no se pide: se
 * genera a partir de {@code nombre} + {@code apellido}. Se admite un
 * {@code login} explícito para mantener compatibilidad con clientes previos.
 */
@Data
public class UsuarioAltaRequest {

    /** Opcional: si viene vacío se genera uno único a partir de nombre y apellido. */
    private String login;

    @NotBlank
    private String password;

    @NotBlank
    private String nombre;

    /** Obligatorio sólo cuando no se envía un login explícito. */
    private String apellido;

    private Integer dependenciaId;

    private Integer geograficaId;

    private Byte imprimeChequera;

    private Byte numeroOpManual;

    private Byte habilitaOpEliminacion;

    private Byte eliminaChequera;

    private Byte modificaChequera;

    private String googleMail;

    private Byte activo;

    private Byte administrador;

    private Byte usuarioExterno;
}
