package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Fila del padrón de generación masiva (preview). Refleja la serie del padrón de altas y, si ya
 * existe, el borrador abierto asociado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneracionPadronItem {

    private Long chequeraId;
    private BigDecimal personaId;
    private Integer documentoId;
    private String personaNombre;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private String tipoChequeraNombre;
    private Integer claseChequeraId;
    private String claseChequeraNombre;
    private Long chequeraSerieId;
    private Integer cursoId;
    private Integer arancelTipoId;
    private String arancelTipoNombre;
    private Integer alternativaId;
    private Byte enviado;
    private Long borradorAbiertoId;
}
