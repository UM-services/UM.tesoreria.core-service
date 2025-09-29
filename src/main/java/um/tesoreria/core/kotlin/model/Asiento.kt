package um.tesoreria.core.kotlin.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.GenerationType
import java.time.OffsetDateTime
import jakarta.persistence.OneToOne
import jakarta.persistence.JoinColumn
import um.tesoreria.core.util.Jsonifier

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["fecha", "orden"]),
        UniqueConstraint(columnNames = ["fechaContra", "ordenContra"])
    ]
)
data class Asiento(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var asientoId: Long? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fecha: OffsetDateTime? = null,

    var orden: Int = 0,
    var vinculo: String = "",

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaContra: OffsetDateTime? = null,

    var ordenContra: Int? = null,
    var trackId: Long? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "trackId", insertable = false, updatable = false)
    var track: Track? = null

) : Auditable() {
    fun jsonify(): String {
        return Jsonifier.builder(this).build()
    }

    companion object {
        fun builder() = Builder()
    }

    class Builder {
        private var asientoId: Long? = null
        private var fecha: OffsetDateTime? = null
        private var orden: Int = 0
        private var vinculo: String = ""
        private var fechaContra: OffsetDateTime? = null
        private var ordenContra: Int? = null
        private var trackId: Long? = null
        private var track: Track? = null

        fun asientoId(asientoId: Long?) = apply { this.asientoId = asientoId }
        fun fecha(fecha: OffsetDateTime?) = apply { this.fecha = fecha }
        fun orden(orden: Int) = apply { this.orden = orden }
        fun vinculo(vinculo: String) = apply { this.vinculo = vinculo }
        fun fechaContra(fechaContra: OffsetDateTime?) = apply { this.fechaContra = fechaContra }
        fun ordenContra(ordenContra: Int?) = apply { this.ordenContra = ordenContra }
        fun trackId(trackId: Long?) = apply { this.trackId = trackId }
        fun track(track: Track?) = apply { this.track = track }

        fun build() = Asiento(
            asientoId,
            fecha,
            orden,
            vinculo,
            fechaContra,
            ordenContra,
            trackId,
            track
        )
    }
}