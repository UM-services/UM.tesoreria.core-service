package um.tesoreria.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import um.tesoreria.core.kotlin.model.Auditable;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ReciboMessageCheck extends Auditable {

    @Id
    private UUID reciboMessageCheckId;
    private Long facturacionElectronicaId;
    private Long chequeraPagoId;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private Integer productoId;
    private Integer alternativaId;
    private Integer cuotaId;
    private String payload;

}
