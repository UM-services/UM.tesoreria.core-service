package um.tesoreria.core.hexagonal.documento.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import um.tesoreria.core.model.Auditable;

@Getter
@Setter
@Entity
@Table(name = "documento")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoEntity extends Auditable {

    @Id
    @Column(name = "doc_id")
    private Integer documentoId;

    @Column(name = "doc_nombre")
    @Builder.Default
    private String nombre = "";

}
