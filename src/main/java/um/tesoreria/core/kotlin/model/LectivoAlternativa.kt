package um.tesoreria.core.kotlin.model

import jakarta.persistence.*

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["lea_fac_id", "lea_lec_id", "lea_tch_id", "lea_pro_id", "lea_alt_id"])
    ]
)
data class LectivoAlternativa(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lea_id")
    var lectivoAlternativaId: Long? = null,

    @Column(name = "lea_fac_id")
    var facultadId: Int? = null,

    @Column(name = "lea_lec_id")
    var lectivoId: Int? = null,

    @Column(name = "lea_tch_id")
    var tipoChequeraId: Int? = null,

    @Column(name = "lea_pro_id")
    var productoId: Int? = null,

    @Column(name = "lea_alt_id")
    var alternativaId: Int? = null,

    @Column(name = "lea_titulo")
    var titulo: String? = "",

    @Column(name = "lea_cuotas")
    var cuotas: Int? = 0,

    @Column(name = "lea_descpterm")
    var descuentoPagoTermino: Int? = 0

) : Auditable()
