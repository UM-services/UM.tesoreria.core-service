package um.tesoreria.core.model;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.kotlin.model.Auditable;
import um.tesoreria.core.kotlin.model.TipoChequera;

import java.util.UUID;

@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"tipoChequeraId", "alternativaId"}))
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TipoChequeraMercadoPagoCreditCard extends Auditable {

    @Id
    private UUID id;

    private Integer tipoChequeraId;
    private Integer alternativaId;
    private Integer installments;
    private Integer defaultInstallments;

    @Builder.Default
    private Byte active = 1;

    @OneToOne(optional = true)
    @JoinColumn(name = "tipoChequeraId", insertable = false, updatable = false)
    private TipoChequera tipoChequera;

}
