package um.tesoreria.core.hexagonal.auth.infrastructure.web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private Long userId;
    private String login;
    private String nombre;
    private Integer dependenciaId;
    private Integer geograficaId;
    private String sede;
    private Byte imprimeChequera;
    private Byte numeroOpManual;
    private Byte habilitaOpEliminacion;
    private Byte eliminaChequera;
    private Byte modificaChequera;
    private Byte administrador;
    private Byte usuarioExterno;
    private Byte debeCambiarClave;

}
