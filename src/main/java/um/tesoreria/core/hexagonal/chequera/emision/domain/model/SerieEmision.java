package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SerieEmision {

    private Long chequeraId;
    private Integer facultadId;
    private String facultadNombre;
    private Integer tipoChequeraId;
    private String tipoChequeraNombre;
    private Long chequeraSerieId;
    private Integer claseChequeraId;
    private Integer geograficaId;
    private String geograficaNombre;
    private Integer arancelTipoId;
    private String arancelTipoDescripcion;
    private Integer alternativaId;
    private Integer cursoId;
    private OffsetDateTime fecha;
    private OffsetDateTime ultimoEnvio;
    private Byte enviado;
    private Byte retenida;
    private boolean pagada;
    private boolean deBaja;
    private OffsetDateTime bajaFecha;
    private String bajaObservaciones;
}
