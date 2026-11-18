package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.model.Auditable;
import um.tesoreria.core.util.Jsonifyable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "chequera_borrador_cuota",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "borrador_id", "producto_id", "alternativa_id", "cuota_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorradorCuotaEntity extends Auditable implements Jsonifyable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chequera_borrador_cuota_id")
    private Long borradorCuotaId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "borrador_id", nullable = false)
    private BorradorChequeraEntity borrador;

    @Column(name = "chequera_cuota_id")
    private Long chequeraCuotaId;

    @Column(name = "producto_id")
    private Integer productoId;

    @Column(name = "alternativa_id")
    private Integer alternativaId;

    @Column(name = "cuota_id")
    private Integer cuotaId;

    @Column(name = "mes")
    private Integer mes;

    @Column(name = "anio")
    private Integer anio;

    @Column(name = "vencimiento_1")
    private OffsetDateTime vencimiento1;

    @Column(name = "vencimiento_2")
    private OffsetDateTime vencimiento2;

    @Column(name = "vencimiento_3")
    private OffsetDateTime vencimiento3;

    @Column(name = "importe_original_1")
    private BigDecimal importe1Original;

    @Column(name = "importe_original_2")
    private BigDecimal importe2Original;

    @Column(name = "importe_original_3")
    private BigDecimal importe3Original;

    @Column(name = "importe_borrador_1")
    private BigDecimal importe1Borrador;

    @Column(name = "importe_borrador_2")
    private BigDecimal importe2Borrador;

    @Column(name = "importe_borrador_3")
    private BigDecimal importe3Borrador;

    @Column(name = "arancel_tipo_id")
    private Integer arancelTipoId;

    @Column(name = "tramo_id")
    private Integer tramoId;

    @Column(name = "descuento_pt")
    private BigDecimal descuentoPagoTermino;

    @Column(name = "pagado")
    private Byte pagado;

    @Column(name = "baja")
    private Byte baja;

    @Column(name = "manual")
    private Byte manual;

    @Column(name = "compensada")
    private Byte compensada;

    @Column(name = "modificado")
    private Byte modificado;

    @Column(name = "bloqueado")
    private Byte bloqueado;
}
