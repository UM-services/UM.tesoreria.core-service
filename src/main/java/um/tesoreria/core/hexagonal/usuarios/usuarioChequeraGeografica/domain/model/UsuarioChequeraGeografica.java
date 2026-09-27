package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.dependencias.geografica.domain.model.Geografica;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioChequeraGeografica {
    private Long usuarioChequeraGeograficaId;
    private Long userId;
    private Integer geograficaId;
    private LocalDateTime created;
    private LocalDateTime updated;
    // Excepción cross-slice autorizada: anclar el dominio de dependencias.geografica
    // (patrón de los slices hermanos usuarioChequeraClaseChequera / usuarioChequeraFacultad).
    private Geografica geografica;
}
