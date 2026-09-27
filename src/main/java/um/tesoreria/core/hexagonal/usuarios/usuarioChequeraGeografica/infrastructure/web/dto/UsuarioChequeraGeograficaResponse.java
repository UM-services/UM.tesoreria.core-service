package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.dependencias.geografica.infrastructure.web.dto.GeograficaResponse;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioChequeraGeograficaResponse {
    private Long usuarioChequeraGeograficaId;
    private Long userId;
    private Integer geograficaId;
    private LocalDateTime created;
    private LocalDateTime updated;
    // Excepción cross-slice autorizada: expone el DTO de dependencias.geografica
    // (patrón de los slices hermanos usuarioChequeraClaseChequera / usuarioChequeraFacultad).
    private GeograficaResponse geografica;
}
