package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Débito a enviar al banco (Débito Directo o VISA).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebitoPendiente {

    private Long debitoId;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private Integer productoId;
    private Integer alternativaId;
    private Integer cuotaId;
    private Integer debitoTipoId;
    private String cbu;
    private String numeroTarjeta;
    private OffsetDateTime fechaVencimiento;
    private OffsetDateTime fechaEnvio;
    private OffsetDateTime fechaBaja;
    private Byte rechazado;

}
