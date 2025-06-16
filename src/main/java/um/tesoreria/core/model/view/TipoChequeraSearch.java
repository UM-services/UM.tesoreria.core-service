package um.tesoreria.core.model.view;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import um.tesoreria.core.kotlin.model.Auditable;

@Data
@Entity
@Table(name = "vw_tipo_chequera_search")
@Immutable
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class TipoChequeraSearch extends Auditable {

    @Id
    private Integer tipoChequeraId;

    private String nombre;
    private String prefijo;
    private Integer geograficaId;
    private Integer claseChequeraId;
    private Byte imprimir;
    private Byte contado;
    private Byte multiple;
    private String emailCopia;
    private String search;

}
