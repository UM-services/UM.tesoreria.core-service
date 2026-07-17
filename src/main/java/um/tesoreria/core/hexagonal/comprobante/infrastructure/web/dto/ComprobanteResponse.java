package um.tesoreria.core.hexagonal.comprobante.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprobanteResponse {
    private Integer comprobanteId;
    private String descripcion;
    private Integer tipoTransaccionId;
    private Byte ordenPago;
    private Byte aplicaPendiente;
    private Byte cuentaCorriente;
    private Byte debita;
    private Long diasVigencia;
    private Byte facturacionElectronica;
    private Integer comprobanteAfipId;
    private Integer puntoVenta;
    private String letraComprobante;
}
