package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Mapeo propio de la tabla bancaria, con columnas explícitas (ddl-auto: none) y en minúscula, con los mismos nombres
 * lógicos que Bancaria.kt: Hibernate rechaza dos entidades sobre la misma tabla que nombran distinto una columna.
 * No extiende Auditable: created es NULL en las filas históricas (allí es nullable=false) y updated lo maneja MySQL.
 */
@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "bancaria")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BancariaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cub_id")
    private Long bancariaId;

    @Column(name = "cub_banco")
    private String banco;

    @Column(name = "cub_numero")
    private String numero;

    @Column(name = "cub_cbu")
    private String cbu;

    @Column(name = "cub_titular")
    private String titular;

    @Column(name = "cub_cuit")
    private String cuit;

    @Column(name = "cub_pla_cuenta")
    private BigDecimal numeroCuenta;

    @Column(name = "cub_prv_id")
    private Integer proveedorId;

    /** Se carga solo en el alta y no se reescribe. */
    @Column(name = "created", updatable = false)
    private LocalDateTime created;

    /** ON UPDATE CURRENT_TIMESTAMP: la asigna MySQL; se relee con refresh después de escribir. */
    @Column(name = "updated", insertable = false, updatable = false)
    private LocalDateTime updated;

    @Column(name = "uuid", updatable = false)
    private String uuid;
}
