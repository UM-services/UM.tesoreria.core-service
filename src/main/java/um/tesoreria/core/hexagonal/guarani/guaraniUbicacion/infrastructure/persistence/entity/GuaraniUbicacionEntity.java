package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.hexagonal.dependencias.geografica.infrastructure.persistence.entity.GeograficaEntity;
import um.tesoreria.core.model.Auditable;

@Getter
@Setter
@Entity
@Table(name = "guarani_ubicacion", uniqueConstraints = @UniqueConstraint(columnNames = "ubicacion"))
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuaraniUbicacionEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guarani_ubicacion_id")
    private Integer guaraniUbicacionId;

    @Column(name = "ubicacion")
    private Integer ubicacion;

    @Column(name = "geografica_id")
    private Integer geograficaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "geografica_id", insertable = false, updatable = false)
    private GeograficaEntity geografica;

}
