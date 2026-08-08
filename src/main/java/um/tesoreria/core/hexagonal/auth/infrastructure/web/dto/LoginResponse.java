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
    private String nombre;
    private Integer geograficaId;
    private String sede;

}
