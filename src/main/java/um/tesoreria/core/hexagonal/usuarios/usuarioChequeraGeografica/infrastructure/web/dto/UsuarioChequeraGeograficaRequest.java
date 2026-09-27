package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioChequeraGeograficaRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Integer geograficaId;

}
