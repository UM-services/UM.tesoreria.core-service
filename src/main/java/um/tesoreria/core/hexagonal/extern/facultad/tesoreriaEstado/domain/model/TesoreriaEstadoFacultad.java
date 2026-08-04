package um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.domain.model;

import lombok.*;
import um.tesoreria.core.util.Jsonifyable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TesoreriaEstadoFacultad implements Jsonifyable {

    private Long tesoreriaEstadoId;
    private Integer facultadId;
    private BigDecimal personaId;
    private Integer documentoId;
    private BigDecimal deuda;

    @Builder.Default
    private Byte manual = 0;

    @Builder.Default
    private Byte importado = 0;

    @Builder.Default
    private String observaciones = "";

    private OffsetDateTime fechaTope;

    @Builder.Default
    private String uuid = "";

}
