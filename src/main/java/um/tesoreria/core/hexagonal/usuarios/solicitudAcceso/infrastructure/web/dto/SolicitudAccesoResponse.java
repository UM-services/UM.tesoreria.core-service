package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudAccesoResponse {

    private Long solicitudAccesoId;
    private Long userId;
    private String aplicacion;
    private String clave;
    private String modulo;
    private String comentario;
    private String estado;
    private String observacion;
    private Long resueltoPor;
    private LocalDateTime resuelto;
    private LocalDateTime created;
    private LocalDateTime updated;
}
