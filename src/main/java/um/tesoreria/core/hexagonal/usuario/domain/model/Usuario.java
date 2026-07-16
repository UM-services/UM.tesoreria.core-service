package um.tesoreria.core.hexagonal.usuario.domain.model;

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
}
