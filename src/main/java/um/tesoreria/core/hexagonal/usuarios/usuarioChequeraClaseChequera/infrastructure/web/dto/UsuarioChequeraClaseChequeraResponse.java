package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.web.dto.ClaseChequeraResponse;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioChequeraClaseChequeraResponse {
    private Long usuarioChequeraClaseChequeraId;
    private Long userId;
    private Integer claseChequeraId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private ClaseChequeraResponse claseChequera;
}
