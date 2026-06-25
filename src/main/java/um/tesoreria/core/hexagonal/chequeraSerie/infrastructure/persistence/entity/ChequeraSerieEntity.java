package um.tesoreria.core.hexagonal.chequeraSerie.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.hexagonal.facultad.infrastructure.persistence.entity.FacultadEntity;
import um.tesoreria.core.hexagonal.geografica.infrastructure.persistence.entity.GeograficaEntity;
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.entity.PersonaEntity;
import um.tesoreria.core.hexagonal.arancelTipo.infrastructure.persistence.entity.ArancelTipoEntity;
import um.tesoreria.core.model.Auditable;
import um.tesoreria.core.hexagonal.domicilio.infrastructure.persistence.entity.DomicilioEntity;
import um.tesoreria.core.kotlin.model.Lectivo;
import um.tesoreria.core.kotlin.model.TipoChequera;
import um.tesoreria.core.util.Jsonifier;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "chequera_serie", uniqueConstraints = {@UniqueConstraint(columnNames = {"chs_fac_id", "chs_tch_id", "chs_id"})})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChequeraSerieEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clave")
    private Long chequeraId;

    @Column(name = "chs_fac_id")
    private Integer facultadId;

    @Column(name = "chs_tch_id")
    private Integer tipoChequeraId;

    @Column(name = "chs_id")
    private Long chequeraSerieId;

    @Column(name = "chs_per_id")
    private BigDecimal personaId;

    @Column(name = "chs_doc_id")
    private Integer documentoId;

    @Column(name = "chs_lec_id")
    private Integer lectivoId;

    @Column(name = "chs_art_id")
    private Integer arancelTipoId;

    @Column(name = "chs_cur_id")
    private Integer cursoId;

    @Column(name = "chs_asentado")
    private Byte asentado;

    @Column(name = "chs_geo_id")
    private Integer geograficaId;

    @Column(name = "chs_fecha")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fecha;

    @Column(name = "chs_cuotasp")
    private Integer cuotasPagadas;

    @Column(name = "chs_observ")
    private String observaciones;

    @Column(name = "chs_alt_id")
    private Integer alternativaId;

    @Column(name = "chs_algopagado")
    private Byte algoPagado;

    @Column(name = "chs_tim_id")
    private Integer tipoImpresionId;

    @Column(name = "flag_paypertic")
    @Builder.Default
    private Byte flagPayperTic = 0;

    private String usuarioId;

    @Builder.Default
    private Byte enviado = 0;

    @Builder.Default
    private Byte retenida = 0;

    private Long version;

    @Builder.Default
    private Byte hpum = 0;

    @Builder.Default
    private BigDecimal becaPorcentaje = BigDecimal.ZERO;

    private String becaResolucion;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime becaFecha;

    private Long becaUserId;

    @Transient
    @Builder.Default
    private int cuotasDeuda = 0;

    @Transient
    @Builder.Default
    private BigDecimal importeDeuda = BigDecimal.ZERO;

    @Transient
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime ultimoEnvio;

    @OneToOne(optional = true)
    @JoinColumn(name = "chs_fac_id", insertable = false, updatable = false)
    private FacultadEntity facultad;

    @OneToOne(optional = true)
    @JoinColumn(name = "chs_tch_id", insertable = false, updatable = false)
    private TipoChequera tipoChequera;

    @OneToOne(optional = true)
    @JoinColumns({
            @JoinColumn(name = "chs_per_id", referencedColumnName = "per_id", insertable = false, updatable = false),
            @JoinColumn(name = "chs_doc_id", referencedColumnName = "per_doc_id", insertable = false, updatable = false)
    })
    private PersonaEntity persona;

    @OneToOne(optional = true)
    @JoinColumns({
            @JoinColumn(name = "chs_per_id", referencedColumnName = "dom_per_id", insertable = false, updatable = false),
            @JoinColumn(name = "chs_doc_id", referencedColumnName = "dom_doc_id", insertable = false, updatable = false)
    })
    private DomicilioEntity domicilio;

    @OneToOne(optional = true)
    @JoinColumn(name = "chs_lec_id", insertable = false, updatable = false)
    private Lectivo lectivo;

    @OneToOne(optional = true)
    @JoinColumn(name = "chs_art_id", insertable = false, updatable = false)
    private ArancelTipoEntity arancelTipo;

    @OneToOne(optional = true)
    @JoinColumn(name = "chs_geo_id", insertable = false, updatable = false)
    private GeograficaEntity geografica;

    public String getPersonaKey() {
        return this.personaId.toString() + "." + this.documentoId;
    }

    public String getFacultadKey() {
        return this.facultadId.toString() + "." + this.lectivoId + "." + this.geograficaId + "." + this.getPersonaKey();
    }

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }

}
