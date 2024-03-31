package um.tesoreria.core.extern.model.kotlin

data class CarreraFacultad(

    var facultadId: Int? = null,
    var planId: Int? = null,
    var carreraId: Int? = null,
    var uniqueId: Long? = null,
    var nombre: String? = null,
    var iniciales: String? = null,
    var titulo: String? = null,
    var trabajoFinal: Byte? = null,
    var resolucion: String? = null,
    var chequeraUnica: Byte? = null,
    var bloqueId: Int? = null,
    var obligatorias: Int? = null,
    var optativas: Int? = null,
    var vigente: Byte? = null,

    ) {

    fun getCarreraKey(): String {
        return this.facultadId.toString() + "." + this.planId + "." + this.carreraId
    }
}
