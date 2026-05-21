package um.tesoreria.core.hexagonal.baja.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.hexagonal.chequeraSerie.infrastructure.persistence.entity.ChequeraSerieEntity;
import um.tesoreria.core.hexagonal.facultad.infrastructure.persistence.entity.FacultadEntity;
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.entity.PersonaEntity;
import um.tesoreria.core.kotlin.model.Auditable;
import um.tesoreria.core.kotlin.model.Lectivo;
import um.tesoreria.core.kotlin.model.TipoChequera;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "baja", uniqueConstraints = {@UniqueConstraint(columnNames = {"baj_fac_id", "baj_tch_id", "baj_chs_id"})})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BajaEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bajaId;

    @Column(name = "baj_fac_id")
    private Integer facultadId;

    @Column(name = "baj_tch_id")
    private Integer tipoChequeraId;

    @Column(name = "baj_chs_id")
    private Long chequeraSerieId;

    private Long chequeraId;

    @Column(name = "baj_lec_id")
    private Integer lectivoId;

    @Column(name = "baj_per_id")
    private BigDecimal personaId;

    @Column(name = "baj_doc_id")
    private Integer documentoId;

    @Column(name = "baj_fecha")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime fecha;

    @Column(name = "baj_observaciones")
    private String observaciones;

    @Builder.Default
    private Byte egresado = 0;

    @OneToOne(optional = true)
    @JoinColumn(name = "baj_fac_id", insertable = false, updatable = false)
    private FacultadEntity facultad;

    @OneToOne(optional = true)
    @JoinColumn(name = "baj_tch_id", insertable = false, updatable = false)
    private TipoChequera tipoChequera;

    @OneToOne(optional = true)
    @JoinColumn(name = "baj_lec_id", insertable = false, updatable = false)
    private Lectivo lectivo;

    @OneToOne(optional = true)
    @JoinColumns({
            @JoinColumn(name = "baj_per_id", referencedColumnName = "per_id", insertable = false, updatable = false),
            @JoinColumn(name = "baj_doc_id", referencedColumnName = "per_doc_id", insertable = false, updatable = false)
    })
    private PersonaEntity persona;

    @OneToOne(optional = true)
    @JoinColumn(name = "chequeraId", insertable = false, updatable = false)
    private ChequeraSerieEntity chequeraSerie;

}
