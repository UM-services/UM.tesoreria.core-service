package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.math.BigDecimal;

/**
 * Base de las 5 vistas rama de la traza contable de una orden de pago
 * ({@code vw_trazaContableD*}). Todas exponen las mismas columnas y una fila por línea de
 * {@code movcon}, por eso {@code id} es la clave.
 *
 * <p>Se leen por separado (una consulta por rama con {@code MvP_ID = ?}) en lugar de la vista
 * {@code vw_trazaContablePagosConsolidada}: esa vista es un {@code UNION} de las 5 más dos
 * {@code GROUP BY}, y MySQL no empuja el filtro por orden de pago (10 s por OP contra 45 ms de las
 * cinco ramas juntas).</p>
 */
@MappedSuperclass
public abstract class TrazaContableRamaEntity {

    @Id
    @Column(name = "id")
    private Integer movimientoId;

    @Column(name = "MvP_ID")
    private Long proveedorMovimientoId;

    @Column(name = "MCo_Pla_Cuenta")
    private BigDecimal numeroCuenta;

    @Column(name = "MCo_Debita")
    private Integer debita;

    @Column(name = "MCo_Importe")
    private BigDecimal importe;

}
