package um.tesoreria.core.kotlin.model.internal

import com.fasterxml.jackson.annotation.JsonFormat
import um.tesoreria.core.kotlin.model.ChequeraCuota
import java.math.BigDecimal
import java.time.LocalTime
import java.time.OffsetDateTime

data class ClickPagosEntity(

    var tipoRegistro: String = "",

    // Header
    var codigoBoton: Int = 0,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaProceso: OffsetDateTime? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaEnvio: OffsetDateTime? = null,
    var numeroLote: Int = 0,

    // Trailer
    var cantidadRegistros: Int = 0,
    var importe: BigDecimal = BigDecimal.ZERO,
    var cantidadTransacciones: Int = 0,

    // Datos
    var r: String = "R",
    var codigoTerminal: Int = 0,
    var adicional1: String = "",
    var codigoSucursal: Int = 0,
    var relleno1: Long = 0,
    var transaccion: Long = 0,
    var codigoOperacion: String = "",
    var tipoTransaccion: Int = 0,
    var relleno2: Int = 0,
    var codigoEnte: Int = 0,
    var adicional2: String = "",
    var adicional3: BigDecimal = BigDecimal.ZERO,
    var adicional4: BigDecimal = BigDecimal.ZERO,
    var moneda: Int = 0,
    var adicional5: Int = 0,
    var relleno3: Int = 0,
    var relleno4: String = "",
    var relleno5: Int = 0,
    var comercioId: Long = 0,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    var horaTransaccion: LocalTime? = null,
    var relleno6: Int = 0,
    var relleno7: Int = 0,
    var relleno8: Int = 0,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaLiquidacion: OffsetDateTime? = null,
    var adicional6: Long = 0,
    var relleno9: Int = 0,
    var codigoBarra: String = "",
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaPago: OffsetDateTime? = null,
    var modoPago: String = "",
    var adicional7: Long = 0,
    var transaccionProcesadorPagoId: BigDecimal = BigDecimal.ZERO,
    var formaPago: Int = 0,
    var adicional8: Int = 0,
    var adicional9: String = "",
    var adicional10: String = "",
    var adicional11: Long = 0,
    var transaccionEnteId: String = "",
    var clave1: String = "",
    var clave2: String = "",
    var facultadId: Int = 0,
    var tipoChequeraId: Int = 0,
    var chequeraSerieId: Long = 0,
    var alternativaId: Int = 0,
    var productoId: Int = 0,
    var cuotaId: Int = 0,
    var clave3: String = "",
    var descripcion: String = "",
    var chequeraCuota: ChequeraCuota? = null,
    var processed: Boolean = false

)
