package um.tesoreria.core.kotlin.repository

import org.springframework.data.jpa.repository.JpaRepository
import um.tesoreria.core.model.ReciboMessageCheck
import java.util.Optional
import java.util.UUID

interface ReciboMessageCheckRepository : JpaRepository<ReciboMessageCheck, UUID> {

    fun findByReciboMessageCheckId(reciboMessageCheckId: UUID): Optional<ReciboMessageCheck?>?

}