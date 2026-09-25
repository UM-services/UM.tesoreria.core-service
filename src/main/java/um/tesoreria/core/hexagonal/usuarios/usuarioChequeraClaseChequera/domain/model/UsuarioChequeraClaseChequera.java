package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioChequeraClaseChequera {
    private Long usuarioChequeraClaseChequeraId;
    private Long userId;
    private Integer claseChequeraId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private ClaseChequera claseChequera;
}
