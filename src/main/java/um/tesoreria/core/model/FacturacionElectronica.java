package um.tesoreria.core.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.*;
import um.tesoreria.core.kotlin.model.Auditable;
import um.tesoreria.core.kotlin.model.ChequeraPago;
import um.tesoreria.core.kotlin.model.Comprobante;
import um.tesoreria.core.util.Jsonifier;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturacionElectronica extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facturacionElectronicaId;

    private Long chequeraPagoId;
    private Integer comprobanteId;
    private Long numeroComprobante;
    private BigDecimal personaId;
    private String tipoDocumento;
    private String apellido;
    private String nombre;
    private String cuit;
    private String condicionIva;
    private BigDecimal importe;
    private String cae;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime fechaRecibo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime fechaVencimientoCae;
    private Byte enviada;
    private Integer retries;

    @OneToOne(optional = true)
    @JoinColumn(name = "chequeraPagoId", insertable = false, updatable = false)
    private ChequeraPago chequeraPago;

    @OneToOne(optional = true)
    @JoinColumn(name = "comprobanteId", insertable = false, updatable = false)
    private Comprobante comprobante;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }

}
