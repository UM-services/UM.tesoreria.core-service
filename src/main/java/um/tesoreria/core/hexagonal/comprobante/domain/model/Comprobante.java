package um.tesoreria.core.hexagonal.comprobante.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comprobante {
    private Integer comprobanteId;
    @Builder.Default
    private String descripcion = "";
    private Integer tipoTransaccionId;
    @Builder.Default
    private Byte ordenPago = 0;
    @Builder.Default
    private Byte aplicaPendiente = 0;
    @Builder.Default
    private Byte cuentaCorriente = 0;
    @Builder.Default
    private Byte debita = 0;
    @Builder.Default
    private Long diasVigencia = 0L;
    @Builder.Default
    private Byte facturacionElectronica = 0;
    private Integer comprobanteAfipId;
    private Integer puntoVenta;
    private String letraComprobante;
}
