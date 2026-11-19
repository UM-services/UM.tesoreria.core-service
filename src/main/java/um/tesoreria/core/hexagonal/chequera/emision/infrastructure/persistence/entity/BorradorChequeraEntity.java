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
    @Column(name = "chequera_borrador_id")
    private Long borradorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoBorrador estado;

    @Column(name = "persona_id")
    private BigDecimal personaId;

    @Column(name = "documento_id")
    private Integer documentoId;

    @Column(name = "facultad_id")
    private Integer facultadId;

    @Column(name = "lectivo_id")
    private Integer lectivoId;

    @Column(name = "tipo_chequera_id")
    private Integer tipoChequeraId;

    @Column(name = "arancel_tipo_id")
    private Integer arancelTipoId;

    @Column(name = "alternativa_id")
    private Integer alternativaId;

    @Column(name = "curso_id")
    private Integer cursoId;

    @Column(name = "chequera_serie_id")
    private Long chequeraSerieId;

    @Column(name = "fecha")
    private OffsetDateTime fecha;

    @Column(name = "hpum")
    private Byte hpum;

    @Column(name = "beca_porcentaje")
    private BigDecimal becaPorcentaje;

    @Column(name = "beca_resolucion")
    private String becaResolucion;

    @Column(name = "beca_fecha")
    private OffsetDateTime becaFecha;

    @Column(name = "beca_user_id")
    private Long becaUserId;

    @Column(name = "calcula_beca")
    private Byte calculaBeca;

    @Column(name = "email_personal")
    private String emailPersonal;

    @Column(name = "email_institucional")
    private String emailInstitucional;

    @Column(name = "email_copia")
    private String emailCopia;

    @Column(name = "incluye_barras")
    private Byte incluyeBarras;

    @Column(name = "copia_facultad")
    private Byte copiaFacultad;

    @Column(name = "chequera_id")
    private Long chequeraId;

    @Column(name = "chequera_serie_id_emitida")
    private Long chequeraSerieIdEmitida;

    @Column(name = "auditoria_id")
    private String auditoriaId;

    @Column(name = "clave_idempotencia")
    private String claveIdempotencia;

    @Column(name = "usuario_id")
    private String usuarioId;

    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name = "emitido_at")
    private LocalDateTime emitidoAt;

    @OneToMany(mappedBy = "borrador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<BorradorCuotaEntity> cuotas = new ArrayList<>();
}
