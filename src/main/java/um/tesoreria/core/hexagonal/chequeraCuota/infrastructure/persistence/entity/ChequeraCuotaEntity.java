package um.tesoreria.core.hexagonal.chequeraCuota.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.hexagonal.chequeraSerie.infrastructure.persistence.entity.ChequeraSerieEntity;
import um.tesoreria.core.hexagonal.facultad.infrastructure.persistence.entity.FacultadEntity;
import um.tesoreria.core.kotlin.model.Producto;
import um.tesoreria.core.kotlin.model.TipoChequera;
import um.tesoreria.core.model.Auditable;
import um.tesoreria.core.util.Jsonifier;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "chequera_cuota", uniqueConstraints = {@UniqueConstraint(columnNames = {"chc_fac_id", "chc_tch_id", "chc_chs_id", "chc_pro_id", "chc_alt_id", "chc_cuo_id"})})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChequeraCuotaEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chc_id")
    private Long chequeraCuotaId;

    private Long chequeraId;

    @Column(name = "chc_fac_id")
    private Integer facultadId;

    @Column(name = "chc_tch_id")
    private Integer tipoChequeraId;

    @Column(name = "chc_chs_id")
    private Long chequeraSerieId;

    @Column(name = "chc_pro_id")
    private Integer productoId;

    @Column(name = "chc_alt_id")
    private Integer alternativaId;

    @Column(name = "chc_cuo_id")
    private Integer cuotaId;

    @Builder.Default
    @Column(name = "chc_mes")
    private Integer mes = 0;

    @Builder.Default
    @Column(name = "chc_anio")
    private Integer anho = 0;

    private Integer arancelTipoId;

    @Column(name = "chc_1er_vencimiento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime vencimiento1;

    @Builder.Default
    @Column(name = "chc_1er_importe")
    private BigDecimal importe1 = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "importe1_original")
    private BigDecimal importe1Original = BigDecimal.ZERO;

    @Column(name = "chc_2do_vencimiento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime vencimiento2;

    @Builder.Default
    @Column(name = "chc_2do_importe")
    private BigDecimal importe2 = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "importe2_original")
    private BigDecimal importe2Original = BigDecimal.ZERO;

    @Column(name = "chc_3er_vencimiento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime vencimiento3;

    @Builder.Default
    @Column(name = "chc_3er_importe")
    private BigDecimal importe3 = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "importe3_original")
    private BigDecimal importe3Original = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "chc_codigo_barras")
    private String codigoBarras = "";

    @Builder.Default
    @Column(name = "chc_i2of5")
    private String i2Of5 = "";

    @Builder.Default
    @Column(name = "chc_pagado")
    private Byte pagado = 0;

    @Builder.Default
    @Column(name = "chc_baja")
    private Byte baja = 0;

    @Builder.Default
    @Column(name = "chc_manual")
    private Byte manual = 0;

    @Builder.Default
    private Byte compensada = 0;

    @Builder.Default
    @Column(name = "chc_tra_id")
    private Integer tramoId = 0;

    @OneToOne(optional = true)
    @JoinColumn(name = "chc_fac_id", insertable = false, updatable = false)
    private FacultadEntity facultad;

    @OneToOne(optional = true)
    @JoinColumn(name = "chc_tch_id", insertable = false, updatable = false)
    private TipoChequera tipoChequera;

    @OneToOne(optional = true)
    @JoinColumn(name = "chc_pro_id", insertable = false, updatable = false)
    private Producto producto;

    @OneToOne(optional = true)
    @JoinColumn(name = "chequeraId", referencedColumnName = "clave", insertable = false, updatable = false)
    private ChequeraSerieEntity chequeraSerie;

    public String cuotaKey() {
        return this.facultadId + "." + this.tipoChequeraId + "." + this.chequeraSerieId + "." + this.productoId + "."
                + this.alternativaId + "." + this.cuotaId;
    }

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }

}
