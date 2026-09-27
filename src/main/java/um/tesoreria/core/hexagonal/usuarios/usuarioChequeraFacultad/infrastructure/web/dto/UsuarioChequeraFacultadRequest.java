package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioChequeraFacultadRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Integer facultadId;

}
