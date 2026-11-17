package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;
import um.tesoreria.core.model.Auditable;
import um.tesoreria.core.util.Jsonifyable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chequera_borrador")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorradorChequeraEntity extends Auditable implements Jsonifyable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chb_id")
    private Long borradorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "chb_estado")
    private EstadoBorrador estado;

    @Column(name = "chb_persona_id")
    private BigDecimal personaId;

    @Column(name = "chb_documento_id")
    private Integer documentoId;

    @Column(name = "chb_facultad_id")
    private Integer facultadId;

    @Column(name = "chb_lectivo_id")
    private Integer lectivoId;

    @Column(name = "chb_tipo_chequera_id")
    private Integer tipoChequeraId;

    @Column(name = "chb_arancel_tipo_id")
    private Integer arancelTipoId;

    @Column(name = "chb_alternativa_id")
    private Integer alternativaId;

    @Column(name = "chb_curso_id")
    private Integer cursoId;

    @Column(name = "chb_chequera_serie_id")
    private Long chequeraSerieId;

    @Column(name = "chb_fecha")
    private OffsetDateTime fecha;

    @Column(name = "chb_hpum")
    private Byte hpum;

    @Column(name = "chb_beca_porcentaje")
    private BigDecimal becaPorcentaje;

    @Column(name = "chb_beca_resolucion")
    private String becaResolucion;

    @Column(name = "chb_beca_fecha")
    private OffsetDateTime becaFecha;

    @Column(name = "chb_beca_user_id")
    private Long becaUserId;

    @Column(name = "chb_calcula_beca")
    private Byte calculaBeca;

    @Column(name = "chb_email_personal")
    private String emailPersonal;

    @Column(name = "chb_email_institucional")
    private String emailInstitucional;

    @Column(name = "chb_email_copia")
    private String emailCopia;

    @Column(name = "chb_incluye_barras")
    private Byte incluyeBarras;

    @Column(name = "chb_copia_facultad")
    private Byte copiaFacultad;

    @Column(name = "chb_chequera_id")
    private Long chequeraId;

    @Column(name = "chb_chequera_serie_id_emitida")
    private Long chequeraSerieIdEmitida;

    @Column(name = "chb_auditoria_id")
    private String auditoriaId;

    @Column(name = "chb_clave_idempotencia")
    private String claveIdempotencia;

    @Column(name = "chb_usuario_id")
    private String usuarioId;

    @Version
    @Column(name = "chb_version")
    private Integer version;

    @Column(name = "emitido_at")
    private LocalDateTime emitidoAt;

    @OneToMany(mappedBy = "borrador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<BorradorCuotaEntity> cuotas = new ArrayList<>();
}
