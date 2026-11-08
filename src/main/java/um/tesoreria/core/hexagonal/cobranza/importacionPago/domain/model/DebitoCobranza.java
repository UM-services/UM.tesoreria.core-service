package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Vista mínima de la tabla {@code debito} que necesita la importación de rendiciones de débito.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebitoCobranza {

    private Long debitoId;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private Integer productoId;
    private Integer alternativaId;
    private Integer cuotaId;
    private Integer debitoTipoId;
    private Byte rechazado;
    private String motivoRechazo;
    private Long archivoBancoId;
    private OffsetDateTime fechaVencimiento;
    private OffsetDateTime fechaEnvio;

}
