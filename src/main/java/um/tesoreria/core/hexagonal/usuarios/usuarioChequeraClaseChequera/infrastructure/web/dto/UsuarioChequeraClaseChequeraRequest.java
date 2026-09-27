package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioChequeraClaseChequeraRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Integer claseChequeraId;

}
