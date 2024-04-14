package um.tesoreria.core.kotlin.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table
data class TipoChequera(

    @Id
    @Column(name = "tch_id")
    var tipoChequeraId: Int? = null,

    @Column(name = "tch_nombre")
    var nombre: String = "",

    @Column(name = "tch_prefijo")
    var prefijo: String = "",

    @Column(name = "tch_geo_id")
    var geograficaId: Int = 1,

    @Column(name = "tch_cch_id")
    var claseChequeraId: Int = 2,

    @Column(name = "tch_imprimir")
    var imprimir: Byte = 0,

    @Column(name = "tch_contado")
    var contado: Byte = 0,

    @Column(name = "multiple")
    var multiple: Byte = 0,

    @OneToOne(optional = true)
    @JoinColumn(name = "tch_geo_id", insertable = false, updatable = false)
    var geografica: Geografica? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "tch_cch_id", insertable = false, updatable = false)
    var claseChequera: ClaseChequera? = null

) : Auditable()
