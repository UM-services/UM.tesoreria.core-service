package um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Configuración administrable de un usuario: datos y flags. No incluye login (identidad)
 * ni clave (se cambia por el flujo de reset aparte). Es un reemplazo total del bloque
 * configurable, por eso los campos obligatorios se validan.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioConfiguracionRequest {

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
    private Byte modificaChequera;
    private String googleMail;
    @NotNull
    private Byte activo;
    @NotNull
    private Byte administrador;
    @NotNull
    private Byte usuarioExterno;

}
