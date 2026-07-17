package um.tesoreria.core.hexagonal.comprobante.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import um.tesoreria.core.kotlin.model.ComprobanteAfip;
import um.tesoreria.core.model.Auditable;

@Entity
@Table(name = "tiposcomprob")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprobanteEntity extends Auditable {

    @Id
    @Column(name = "tco_id")
    private Integer comprobanteId;

    @Column(name = "tco_descripcion")
    @Builder.Default
    private String descripcion = "";

    @Column(name = "tco_modulo")
    private Integer tipoTransaccionId;

    @Column(name = "tco_opago")
    @Builder.Default
    private byte ordenPago = 0;

    @Column(name = "tco_aplicapend")
    @Builder.Default
    private byte aplicaPendiente = 0;

    @Column(name = "tco_ctacte")
    @Builder.Default
    private byte cuentaCorriente = 0;

    @Column(name = "tco_debita")
    @Builder.Default
    private byte debita = 0;

    @Column(name = "tco_diasvigencia")
    @Builder.Default
    private long diasVigencia = 0;

    @Builder.Default
    private byte facturacionElectronica = 0;

    private Integer comprobanteAfipId;

    private Integer puntoVenta;

    private String letraComprobante;

    @OneToOne(optional = true)
    @JoinColumn(name = "comprobanteAfipId", insertable = false, updatable = false)
    private ComprobanteAfip comprobanteAfip;
}
