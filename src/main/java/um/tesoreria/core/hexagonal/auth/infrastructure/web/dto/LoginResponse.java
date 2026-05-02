package um.tesoreria.core.hexagonal.auth.infrastructure.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String token;
    private Long userId;
    private String nombre;
    private String sede;

}
