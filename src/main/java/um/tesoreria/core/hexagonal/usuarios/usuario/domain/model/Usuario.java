package um.tesoreria.core.hexagonal.usuarios.usuario.domain.model;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private Long userId;
    private String login;
    private String password;
    private String nombre;
    /**
     * Apellido recibido en el alta, usado sólo para generar el login y componer
     * {@link #nombre}. No se persiste como columna propia: en la base el nombre
     * completo se guarda en {@code nombre}.
     */
    private String apellido;
    private Integer dependenciaId;
    private Integer geograficaId;
    @Builder.Default
    private Byte imprimeChequera = 0;
    @Builder.Default
    private Byte numeroOpManual = 0;
    @Builder.Default
    private Byte habilitaOpEliminacion = 0;
    @Builder.Default
    private Byte eliminaChequera = 0;
    @Builder.Default
    private Byte modificaChequera = 0;
    private OffsetDateTime lastLog;
    private String googleMail;
    @Builder.Default
    private Byte activo = 1;
    @Builder.Default
    private Byte administrador = 0;
    @Builder.Default
    private Byte usuarioExterno = 0;
    @Builder.Default
    private Byte debeCambiarClave = 0;
}
