package um.tesoreria.rest.kotlin.model

import jakarta.persistence.*


@Entity
@Table(
    name = "movprov_detallevalores",
    uniqueConstraints = [UniqueConstraint(columnNames = ["opv_orp_id", "opv_orden"])]
)
data class ProveedorValor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opv_id")
    var proveedorValorId: Long? = null,

    @Column(name = "opv_orp_id")
    var proveedorMovimientoId: Long? = null,

    @Column(name = "opv_orden")
    var orden: Int = 0,

    @Column(name = "opv_val_id")
    var valorMovimientoId: Long? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "opv_val_id", referencedColumnName = "val_id", insertable = false, updatable = false)
    var valorMovimiento: ValorMovimiento? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "opv_orp_id", referencedColumnName = "mvp_id", insertable = false, updatable = false)
    var proveedorMovimiento: ProveedorMovimiento? = null,

    ) : Auditable()
