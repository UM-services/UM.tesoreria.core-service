package um.tesoreria.core.hexagonal.pagos.valorMovimiento.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;

/**
 * Mapeo propio de {@code valor} (catálogo de tipos de valor). Replica nombres lógicos y columnas
 * de {@code Valor.kt}.
 */
@Getter
@Setter
@Entity
@Table(name = "Valor")
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValorEntity extends Auditable {

    @Id
    @Column(name = "tiv_id")
    private Integer valorId;

    @Column(name = "tiv_concepto")
    private String concepto;

    @Column(name = "tiv_categoria")
    private String categoria;

    @Column(name = "tiv_retardo")
    private Integer retardo;

    @Column(name = "tiv_vencimiento")
    private Integer vencimiento;

    @Column(name = "tiv_prv_id")
    private Integer proveedorId;

    @Column(name = "tiv_cuenta")
    private BigDecimal numeroCuenta;
}
