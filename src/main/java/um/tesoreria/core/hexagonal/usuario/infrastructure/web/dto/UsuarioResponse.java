package um.tesoreria.core.hexagonal.usuario.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Long userId;
    private String login;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime lastLog;
    private String googleMail;
    @Builder.Default
    private Byte activo = 1;
}
