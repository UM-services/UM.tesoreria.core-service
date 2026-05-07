package um.tesoreria.core.hexagonal.auth.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "El usuario es obligatorio")
    private String login;

    @NotBlank(message = "La clave es obligatoria")
    private String password;
}
