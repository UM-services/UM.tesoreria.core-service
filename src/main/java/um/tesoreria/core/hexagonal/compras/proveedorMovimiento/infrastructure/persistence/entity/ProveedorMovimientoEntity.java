package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.hexagonal.comprobante.infrastructure.persistence.entity.ComprobanteEntity;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.entity.ProveedorEntity;
import um.tesoreria.core.hexagonal.geografica.infrastructure.persistence.entity.GeograficaEntity;
import um.tesoreria.core.kotlin.model.ProveedorArticulo;
import um.tesoreria.core.kotlin.model.ProveedorPago;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "movprov")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProveedorMovimientoEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mvp_id")
    private Long proveedorMovimientoId;

    @Column(name = "mvp_prv_id")
    private Integer proveedorId;

    @Builder.Default
    @Column(name = "mvp_nombrebeneficiario")
    private String nombreBeneficiario = "";

    @Column(name = "mvp_tco_id")
    private Integer comprobanteId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    @Column(name = "mvp_fechacomprob")
    private OffsetDateTime fechaComprobante;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    @Column(name = "mvp_fechavenc")
    private OffsetDateTime fechaVencimiento;

    @Builder.Default
    @Column(name = "mvp_prefijo")
    private Integer prefijo = 0;

    @Builder.Default
    @Column(name = "mvp_nrocomprob")
    private Long numeroComprobante = 0L;

    @Builder.Default
    @Column(name = "mvp_netosindescuento")
    private BigDecimal netoSinDescuento = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "mvp_descuento")
    private BigDecimal descuento = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "mvp_neto")
    private BigDecimal neto = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "mvp_importe")
    private BigDecimal importe = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "mvp_cancelado")
    private BigDecimal cancelado = BigDecimal.ZERO;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    @Column(name = "mvp_fechareg")
    private OffsetDateTime fechaContable;

    @Column(name = "mvp_nrocomp")
    private Integer ordenContable;

    @Column(name = "mvp_concepto")
    private String concepto;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    @Column(name = "mvp_anulado")
    private OffsetDateTime fechaAnulacion;

    @Builder.Default
    @Column(name = "mvp_concargo")
    private Byte conCargo = 0;

    @Builder.Default
    @Column(name = "mvp_solicfactura")
    private Byte solicitaFactura = 0;

    private Integer geograficaId;

    @OneToOne(optional = true)
    @JoinColumn(name = "mvp_tco_id", insertable = false, updatable = false)
    private ComprobanteEntity comprobante;

    @OneToOne(optional = true)
    @JoinColumn(name = "mvp_prv_id", insertable = false, updatable = false)
    private ProveedorEntity proveedor;

    @OneToOne(optional = true)
    @JoinColumn(name = "geograficaId", insertable = false, updatable = false)
    private GeograficaEntity geografica;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "fad_mvp_id", insertable = false, updatable = false)
    private List<ProveedorArticulo> proveedorArticulos;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "opc_mvp_id", insertable = false, updatable = false)
    private List<ProveedorPago> ordenPagos;

}
