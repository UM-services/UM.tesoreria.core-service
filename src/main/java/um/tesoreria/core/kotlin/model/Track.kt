package um.tesoreria.core.kotlin.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import um.tesoreria.core.util.Jsonifier

@Entity
data class Track(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var trackId: Long? = null,

    var descripcion: String = ""

) : Auditable() {

    fun jsonify(): String {
        return Jsonifier.builder(this).build()
    }

    companion object {
        fun builder() = Builder()
    }

    class Builder {
        private var trackId: Long? = null
        private var descripcion: String = ""

        fun trackId(trackId: Long?) = apply { this.trackId = trackId }
        fun descripcion(descripcion: String) = apply { this.descripcion = descripcion }

        fun build() = Track(
            trackId,
            descripcion
        )
    }
}