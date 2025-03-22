package um.tesoreria.core.kotlin.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import um.tesoreria.core.model.ChequeraMessageCheck
import java.util.Optional
import java.util.UUID

@Repository
interface ChequeraMessageCheckRepository : JpaRepository<ChequeraMessageCheck, Long> {

    fun findByChequeraMessageCheckId(chequeraMessageCheckId: UUID): Optional<ChequeraMessageCheck?>?

}