package ar.edu.um.tesoreria.rest.model.dto

data class SpoterDataResponse(

	var status: Boolean = false,
	var message: String = "",
	var facultadId: Int? = null,
	var tipoChequeraId: Int? = null,
	var chequeraSerieId: Long? = null

)