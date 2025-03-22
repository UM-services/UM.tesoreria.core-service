package um.tesoreria.core.kotlin.model.dto.message

import java.util.UUID

data class ChequeraMessageDto (
    val uuid: UUID,
    val facultadId: Int,
    val tipoChequeraId: Int,
    val chequeraSerieId: Long,
    val alternativaId: Int,
    val copiaInformes: Boolean,
    val codigoBarras: Boolean,
    val incluyeMatricula: Boolean
) {
    data class Builder(
        var uuid: UUID = UUID.randomUUID(),
        var facultadId: Int = 0,
        var tipoChequeraId: Int = 0,
        var chequeraSerieId: Long = 0,
        var alternativaId: Int = 0,
        var copiaInformes: Boolean = false,
        var codigoBarras: Boolean = false,
        var incluyeMatricula: Boolean = false
    ) {
        fun uuid(uuid: UUID) = apply { this.uuid = uuid }
        fun facultadId(facultadId: Int) = apply { this.facultadId = facultadId }
        fun tipoChequeraId(tipoChequeraId: Int) = apply { this.tipoChequeraId = tipoChequeraId }
        fun chequeraSerieId(chequeraSerieId: Long) = apply { this.chequeraSerieId = chequeraSerieId }
        fun alternativaId(alternativaId: Int) = apply { this.alternativaId = alternativaId }
        fun copiaInformes(copiaInformes: Boolean) = apply { this.copiaInformes = copiaInformes }
        fun codigoBarras(codigoBarras: Boolean) = apply { this.codigoBarras = codigoBarras }
        fun incluyeMatricula(incluyeMatricula: Boolean) = apply { this.incluyeMatricula = incluyeMatricula }
        
        fun build() = ChequeraMessageDto(
            uuid,
            facultadId,
            tipoChequeraId,
            chequeraSerieId,
            alternativaId,
            copiaInformes,
            codigoBarras,
            incluyeMatricula
        )
    }

    companion object {
        fun builder() = Builder()
    }
}
