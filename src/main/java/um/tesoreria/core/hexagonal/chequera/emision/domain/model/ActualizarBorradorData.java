package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Datos editables de un borrador (paso 2 del wizard).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarBorradorData {

    private Integer version;
    private Integer arancelTipoId;
    private Integer alternativaId;
    private Integer cursoId;
    private OffsetDateTime fecha;
    private Byte hpum;
    private BigDecimal becaPorcentaje;
    private String becaResolucion;
    private OffsetDateTime becaFecha;
    private Long becaUserId;
    private Boolean calculaBeca;
    private String emailPersonal;
    private String emailInstitucional;
    private Boolean incluyeBarras;
    private Boolean copiaFacultad;
    @Builder.Default
    private List<BorradorCuota> cuotas = new ArrayList<>();
}
