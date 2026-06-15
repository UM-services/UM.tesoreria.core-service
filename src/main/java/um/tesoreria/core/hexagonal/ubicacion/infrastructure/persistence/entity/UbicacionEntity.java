package um.tesoreria.core.hexagonal.ubicacion.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.model.Auditable;
import um.tesoreria.core.hexagonal.dependencia.infrastructure.persistence.entity.DependenciaEntity;

@Getter
@Setter
@Entity
@Table(name = "ubicacion")
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionEntity extends Auditable {

    @Id
    private Integer ubicacionId;

    private String nombre;
    private Integer dependenciaId;

    @OneToOne(optional = true)
    @JoinColumn(name = "dependenciaId", insertable = false, updatable = false)
    private DependenciaEntity dependencia;

}
