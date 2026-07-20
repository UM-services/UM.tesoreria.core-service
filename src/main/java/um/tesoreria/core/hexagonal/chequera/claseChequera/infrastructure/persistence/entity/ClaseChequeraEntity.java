package um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.model.Auditable;
import um.tesoreria.core.util.Jsonifyable;

@Entity
@Table(name = "clase_chequera")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaseChequeraEntity extends Auditable implements Jsonifyable {

    @Id
    @Column(name = "cch_id")
    private Integer claseChequeraId;

    @Column(name = "cch_nombre")
    private String nombre;

    @Builder.Default
    private Byte preuniversitario = 0;

    @Builder.Default
    private Byte grado = 0;

    @Builder.Default
    private Byte posgrado = 0;

    @Builder.Default
    private Byte curso = 0;

    @Builder.Default
    private Byte secundario = 0;

    @Builder.Default
    private Byte titulo = 0;

    @Builder.Default
    private Byte tramite = 0;

}
