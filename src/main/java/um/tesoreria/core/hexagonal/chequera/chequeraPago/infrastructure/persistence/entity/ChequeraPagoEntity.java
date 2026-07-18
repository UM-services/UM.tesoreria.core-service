package um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.persistence.entity.ChequeraCuotaEntity;
import um.tesoreria.core.hexagonal.chequera.producto.infrastructure.persistence.entity.ProductoEntity;
import um.tesoreria.core.kotlin.model.TipoPago;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "chequera_pago", uniqueConstraints = {@UniqueConstraint(columnNames = {"chp_fac_id", "chp_tch_id", "chp_chs_id", "chp_pro_id", "chp_alt_id", "chp_cuo_id", "chp_orden"})})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChequeraPagoEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clave")
    private Long chequeraPagoId;

    private Long chequeraCuotaId;

    @Column(name = "chp_fac_id")
    private Integer facultadId;

    @Column(name = "chp_tch_id")
    private Integer tipoChequeraId;

    @Column(name = "chp_chs_id")
    private Long chequeraSerieId;

    @Column(name = "chp_pro_id")
    private Integer productoId;

    @Column(name = "chp_alt_id")
    private Integer alternativaId;

    @Column(name = "chp_cuo_id")
    private Integer cuotaId;

    @Column(name = "chp_orden")
    private Integer orden;

    @Builder.Default
    @Column(name = "chp_mes")
    private Integer mes = 0;

    @Builder.Default
    @Column(name = "chp_anio")
    private Integer anho = 0;

    @Column(name = "chp_fecha")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fecha;

    @Column(name = "chp_acred")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime acreditacion;

    @Builder.Default
    @Column(name = "chp_importe")
    private BigDecimal importe = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "chp_path")
    private String path = "";

    @Builder.Default
    @Column(name = "chp_archivo")
    private String archivo = "";

    @Builder.Default
    @Column(name = "chp_observaciones")
    private String observaciones = "";

    @Column(name = "chp_arb_id")
    private Long archivoBancoId;

    @Column(name = "chp_arb_id_acred")
    private Long archivoBancoIdAcreditacion;

    @Builder.Default
    private Integer verificador = 0;

    @Column(name = "chp_tpa_id")
    private Integer tipoPagoId;

    private String idMercadoPago;

    @OneToOne(optional = true)
    @JoinColumn(name = "chp_tpa_id", insertable = false, updatable = false)
    private TipoPago tipoPago;

    @OneToOne(optional = true)
    @JoinColumn(name = "chp_pro_id", insertable = false, updatable = false)
    private ProductoEntity producto;

    @OneToOne(optional = true)
    @JoinColumn(name = "chequeraCuotaId", referencedColumnName = "chc_id", insertable = false, updatable = false)
    private ChequeraCuotaEntity chequeraCuota;

    public String getCuotaKey() {
        return this.facultadId + "." + this.tipoChequeraId + "." + this.chequeraSerieId + "." + this.productoId + "."
                + this.alternativaId + "." + this.cuotaId;
    }

}