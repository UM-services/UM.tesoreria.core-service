package ar.edu.um.tesoreria.rest.kotlin.model

import ar.edu.um.tesoreria.rest.model.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table
data class ChequeraFacturacionElectronica(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var chequeraFacturacionElectronicaId: Long? = null,

    var chequeraId: Long? = null,
    var cuit: String = "",
    var razonSocial: String = "",
    var domicilio: String = "",

    ) : Auditable()
