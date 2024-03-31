package um.tesoreria.core.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(name = "libro_banco", uniqueConstraints = [UniqueConstraint(columnNames = ["lib_cub_id", "lib_fecha", "lib_orden"])])
data class BancoMovimiento(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lib_id")
    var bancoMovimientoId: Long? = null,

    @Column(name = "lib_cub_id")
    var bancariaId: Long? = null,

    @Column(name = "lib_fecha")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fecha: OffsetDateTime? = null,

    @Column(name = "lib_orden")
    var orden: Long = 0,

    @Column(name = "lib_tipo")
    var tipo: String = "",

    @Column(name = "lib_nrocomprobante")
    var numeroComprobante: String = "",

    @Column(name = "lib_concepto")
    var concepto: String = "",

    @Column(name = "lib_importe")
    var importe: BigDecimal = BigDecimal.ZERO,

    @Column(name = "lib_cuentacontable")
    var numeroCuenta: BigDecimal? = null,

    @Column(name = "lib_debita")
    var debita: Byte = 0,

    @Column(name = "lib_fechaconciliacion")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaConciliacion: OffsetDateTime? = null,

    @Column(name = "lib_anulado")
    var anulado: Byte = 0,

    @Column(name = "lib_fechaasiento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaContable: OffsetDateTime? = null,

    @Column(name = "lib_numeroasiento")
    var ordenContable: Int = 0,

    @Column(name = "lib_val_id")
    var valorMovimientoId: Long? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "lib_cuentacontable", insertable = false, updatable = false)
    var cuenta: Cuenta? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "lib_val_id", insertable = false, updatable = false)
    var valorMovimiento: ValorMovimiento? = null,

    ) : Auditable()
