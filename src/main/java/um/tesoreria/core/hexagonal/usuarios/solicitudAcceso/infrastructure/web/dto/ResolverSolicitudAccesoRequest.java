package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
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
public class ResolverSolicitudAccesoRequest {

    /** APROBADA o RECHAZADA. */
    @NotBlank
    private String estado;

    @Size(max = 500)
    private String observacion;

    /** Usuario administrador que resuelve (transitorio hasta el JWT de M3). */
    private Long resueltoPor;
}
