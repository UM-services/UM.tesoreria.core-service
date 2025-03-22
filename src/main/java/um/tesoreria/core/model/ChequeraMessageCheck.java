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
public class ChequeraMessageCheck extends Auditable {

    @Id
    private UUID chequeraMessageCheckId;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private String payload;

}
