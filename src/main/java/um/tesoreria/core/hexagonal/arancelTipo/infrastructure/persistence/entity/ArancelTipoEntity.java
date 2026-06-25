package um.tesoreria.core.hexagonal.arancelTipo.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import um.tesoreria.core.model.Auditable;
import um.tesoreria.core.util.Jsonifier;

@Getter
@Setter
@Entity
@Table(name = "arancel_tipo")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArancelTipoEntity extends Auditable {

    @Id
    @Column(name = "art_id")
    private Integer arancelTipoId;

    @Builder.Default
    @Column(name = "art_descripcion")
    private String descripcion = "";

    @Builder.Default
    private Byte medioArancel = 0;

    private Integer arancelTipoIdCompleto;

    @Builder.Default
    private Byte habilitado = 0;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }

}
