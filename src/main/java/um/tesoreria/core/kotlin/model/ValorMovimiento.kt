package um.tesoreria.core.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(name = "valores")
data class ValorMovimiento(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "val_id")
    var valorMovimientoId: Long? = null,

    @Column(name = "val_tiv_id")
    var valorId: Int? = null,

    @Column(name = "val_fechaemision")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaEmision: OffsetDateTime? = null,

    @Column(name = "val_numero")
    var numero: Long = 0L,

    @Column(name = "val_cuentaorigen")
    var bancariaIdOrigen: Long? = null,

    @Column(name = "val_fechaefectivizacion")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaEfectivizacion: OffsetDateTime? = null,

    @Column(name = "val_importe")
    var importe: BigDecimal = BigDecimal.ZERO,

    @Column(name = "val_estado")
    var estado: String? = null,

    @Column(name = "val_nombretitular")
    var nombreTitular: String = "",

    @Column(name = "val_cuittitular")
    var cuitTitular: String = "",

    @Column(name = "val_reemplazo")
    var reemplazo: Long = 0L,

    @Column(name = "val_fechaasiento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaContable: OffsetDateTime? = null,

    @Column(name = "val_numeroasiento")
    var ordenContable: Int? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaContableAnulacion: OffsetDateTime? = null,

    var ordenContableAnulacion: Int? = null,

    @Column(name = "val_letras")
    var letras: String = "",

    @OneToOne(optional = true)
    @JoinColumn(name = "val_tiv_id", referencedColumnName = "tiv_id", insertable = false, updatable = false)
    var valor: Valor? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "val_cuentaorigen", referencedColumnName = "cub_id", insertable = false, updatable = false)
    var bancaria: Bancaria? = null,

    ) : Auditable()
