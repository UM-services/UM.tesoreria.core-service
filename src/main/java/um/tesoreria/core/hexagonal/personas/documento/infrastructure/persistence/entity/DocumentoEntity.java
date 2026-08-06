package um.tesoreria.core.hexagonal.personas.documento.infrastructure.persistence.entity;

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
    @Column(name = "Doc_ID", nullable = false)
    private Integer documentoId;

    @Column(name = "Doc_Nombre", nullable = false, length = 20)
    @Builder.Default
    private String nombre = "";

    @Column(name = "guarani_tipo_documento")
    private Integer guaraniTipoDocumento;

    @Column(name = "auto_id", nullable = false)
    private Integer autoId;

    @Column(name = "uuid", nullable = false, length = 32)
    @Builder.Default
    private String uuid = "";

}
