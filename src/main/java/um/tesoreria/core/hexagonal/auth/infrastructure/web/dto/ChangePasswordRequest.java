package um.tesoreria.core.hexagonal.auth.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    private Long userId;

    private String login;

    @NotBlank(message = "Clave anterior es obligatoria")
    private String currentPassword;

    @NotBlank(message = "Clave nueva es obligatoria")
    private String newPassword;

    private String reClaveNueva;

    private String nombre;

}
