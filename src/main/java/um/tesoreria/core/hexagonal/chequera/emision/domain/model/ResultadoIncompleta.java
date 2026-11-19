package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resultado de procesar una tarea del job de incompletas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoIncompleta {

    private IncompletasTareaEstado estado;
    private Long borradorId;
    private String codigo;
    private String mensaje;

    public static ResultadoIncompleta borrador(Long borradorId, boolean reutilizado) {
        return ResultadoIncompleta.builder()
                .estado(reutilizado ? IncompletasTareaEstado.BORRADOR_REUTILIZADO : IncompletasTareaEstado.BORRADOR_CREADO)
                .borradorId(borradorId)
                .build();
    }

    public static ResultadoIncompleta emitida(Long borradorId, Long chequeraSerieId) {
        return ResultadoIncompleta.builder()
                .estado(IncompletasTareaEstado.EMITIDA)
                .borradorId(borradorId)
                .mensaje(chequeraSerieId == null ? null : "Chequera " + chequeraSerieId + " emitida")
                .build();
    }

    public static ResultadoIncompleta omitida(String codigo, String mensaje) {
        return ResultadoIncompleta.builder()
                .estado(IncompletasTareaEstado.OMITIDA)
                .codigo(codigo)
                .mensaje(mensaje)
                .build();
    }

    public static ResultadoIncompleta error(String codigo, String mensaje) {
        return ResultadoIncompleta.builder()
                .estado(IncompletasTareaEstado.ERROR)
                .codigo(codigo)
                .mensaje(mensaje)
                .build();
    }
}
