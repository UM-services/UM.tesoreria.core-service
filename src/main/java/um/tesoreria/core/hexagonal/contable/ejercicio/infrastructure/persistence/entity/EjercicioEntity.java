package um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import um.tesoreria.core.model.Auditable;

import java.time.OffsetDateTime;

/**
 * Mapeo propio de la tabla ejercicios, con columnas explícitas (ddl-auto: none). En minúscula: la
 * estrategia de nombres de Spring convertiría Eje_FechaFin en eje_fecha_fin, que no existe en la base.
 * Las órdenes usan el nombre lógico de Ejercicio.kt (la estrategia da orden_contable_*): Hibernate rechaza dos
 * entidades sobre la misma tabla con nombres lógicos distintos para una misma columna.
 */
@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "ejercicios")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EjercicioEntity extends Auditable {

    @Id
    @Column(name = "eje_id")
    private Integer ejercicioId;

    @Column(name = "eje_nombre")
    private String nombre;

    @Column(name = "eje_fechainicio")
    private OffsetDateTime fechaInicio;

    @Column(name = "eje_fechafin")
    private OffsetDateTime fechaFinal;

    @Builder.Default
    @Column(name = "eje_bloqueado")
    private Byte bloqueado = 0;

    @Builder.Default
    @Column(name = "ordenContableResultado")
    private Integer ordenContableResultado = 0;

    @Builder.Default
    @Column(name = "ordenContableBienesUso")
    private Integer ordenContableBienesUso = 0;
}
