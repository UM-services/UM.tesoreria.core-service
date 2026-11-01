package um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginSugeridoResponse {
    private String login;
}
