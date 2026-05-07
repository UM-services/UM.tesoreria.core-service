package um.tesoreria.core.hexagonal.geografica.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.kotlin.model.Auditable;

@Getter
@Setter
@Entity
@Table(name = "geografica")
@NoArgsConstructor
@AllArgsConstructor
public class GeograficaEntity extends Auditable {

    @Id
    @Column(name = "geo_id")
    private Integer geograficaId;

    @Column(name = "geo_nombre")
    private String nombre;

    @Column(name = "geo_sinchequera")
    private Byte sinChequera;

}
