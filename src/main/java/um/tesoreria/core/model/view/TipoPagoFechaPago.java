package um.tesoreria.core.model.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "vw_tipo_pago_fecha_pago")
@Immutable
public class TipoPagoFechaPago {

    @Id
    private String uniqueId;

    private Integer tipoPagoId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime fechaPago;

}
