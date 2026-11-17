package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** Una combinación (fecha, tipo de pago) del job masivo. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsientoIngresoJobTarea {

    private Long tareaId;
    private Long jobId;
    private OffsetDateTime fechaContable;
    private Integer tipoPagoId;
    private AsientoIngresoJobTareaEstado estado;
    private Integer ordenContable;
    private BigDecimal totalDebe;
    private BigDecimal totalHaber;
    private List<CuentaFaltante> faltantes;
    private String errorCodigo;
    private String errorMensaje;
    private OffsetDateTime iniciado;
    private OffsetDateTime finalizado;

}
