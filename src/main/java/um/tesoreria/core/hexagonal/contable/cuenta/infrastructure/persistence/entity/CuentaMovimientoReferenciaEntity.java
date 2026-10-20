package um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.entity;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

/** Proyección de solo lectura de movcon dentro de cuenta, sin dependencia de otro slice. */
@Entity
@Immutable
@Table(name = "movcon")
@Getter
@NoArgsConstructor
public class CuentaMovimientoReferenciaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "mco_pla_cuenta")
    private BigDecimal numeroCuenta;
}
