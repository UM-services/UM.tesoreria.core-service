package um.tesoreria.rest.kotlin.model

import um.tesoreria.rest.model.Auditable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table
data class Facultad(

    @Id
    @Column(name = "fac_id")
    var facultadId: Int? = null,

    @Column(name = "fac_nombre")
    var nombre: String = "",

    @Column(name = "fac_codigo_empresa")
    var codigoempresa: String = "",

    @Column(name = "fac_server")
    var server: String = "",

    @Column(name = "fac_db_adm")
    var dbadm: String = "",

    @Column(name = "fac_dsn")
    var dsn: String = "",

    @Column(name = "fac_pla_cuenta")
    var cuentacontable: BigDecimal = BigDecimal.ZERO,

    @Column(name = "api_server")
    var apiserver: String = "",

    @Column(name = "api_port")
    var apiport: Long = 0L

) : Auditable()
