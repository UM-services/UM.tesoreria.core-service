package um.tesoreria.rest.kotlin.model

import jakarta.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["cha_fac_id", "cha_tch_id", "cha_chs_id", "cha_pro_id", "cha_alt_id"])])
data class ChequeraAlternativa(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "cha_id")
    var chequeraAlternativaId: Long? = null,

    @Column(name = "cha_fac_id")
    var facultadId: Int? = null,

    @Column(name = "cha_tch_id")
    var tipoChequeraId: Int? = null,

    @Column(name = "cha_chs_id")
    var chequeraSerieId: Long? = null,

    @Column(name = "cha_pro_id")
    var productoId: Int? = null,

    @Column(name = "cha_alt_id")
    var alternativaId: Int? = null,

    @Column(name = "cha_titulo")
    var titulo: String = "",

    @Column(name = "cha_cuotas")
    var cuotas: Int = 0,

    ) : Auditable()
