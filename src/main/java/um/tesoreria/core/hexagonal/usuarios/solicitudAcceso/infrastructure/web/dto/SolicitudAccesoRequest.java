package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudAccesoRequest {

    @NotNull
    private Long userId;

    private String aplicacion;

    private String clave;

    private String modulo;

    @Size(max = 500)
    private String comentario;
}
