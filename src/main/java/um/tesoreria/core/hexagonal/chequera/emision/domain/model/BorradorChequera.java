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
 * Borrador persistido de una chequera individual.
 *
 * <p>El borrador es independiente de {@code chequera_serie}/{@code chequera_cuota}:
 * recién al emitir se crea o actualiza la serie real y se escriben las cuotas con su
 * código de barras. Mientras está {@link EstadoBorrador#ABIERTO} puede editarse,
 * completarse y recalcularse sin efectos sobre la cobranza.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorradorChequera {

    private Long borradorId;
    private EstadoBorrador estado;
    private BigDecimal personaId;
    private Integer documentoId;
    private Integer facultadId;
    private String facultadNombre;
    private Integer lectivoId;
    private Integer tipoChequeraId;
    private String tipoChequeraNombre;
    private Integer arancelTipoId;
    private Integer alternativaId;
    private Integer cursoId;
    private Long chequeraSerieId;
    private OffsetDateTime fecha;
    private Byte hpum;
    private BigDecimal becaPorcentaje;
    private String becaResolucion;
    private OffsetDateTime becaFecha;
    private Long becaUserId;
    private boolean calculaBeca;
    private String emailPersonal;
    private String emailInstitucional;
    private String emailCopia;
    private boolean incluyeBarras;
    private boolean copiaFacultad;
    private Long chequeraId;
    private Long chequeraSerieIdEmitida;
    private String auditoriaId;
    private String claveIdempotencia;
    private String usuarioId;
    private Integer version;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime emitidoAt;
    @Builder.Default
    private List<BorradorCuota> cuotas = new ArrayList<>();
    @Builder.Default
    private List<AlternativaEmision> alternativas = new ArrayList<>();
    @Builder.Default
    private List<ArancelTipoEmision> arancelTipos = new ArrayList<>();
    @Builder.Default
    private List<AvisoEmision> avisos = new ArrayList<>();

    public BorradorCuota cuota(Integer productoId, Integer alternativaId, Integer cuotaId) {
        String key = productoId + "." + alternativaId + "." + cuotaId;
        return cuotas.stream().filter(cuota -> cuota.key().equals(key)).findFirst().orElse(null);
    }

    public BigDecimal totalBorrador() {
        if (cuotas == null) {
            return BigDecimal.ZERO;
        }
        return cuotas.stream()
                .map(cuota -> cuota.getImporte1Borrador() == null ? BigDecimal.ZERO : cuota.getImporte1Borrador())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
