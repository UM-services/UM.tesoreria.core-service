package um.tesoreria.rest.kotlin.repository;

import org.springframework.data.jpa.repository.JpaRepository
import um.tesoreria.rest.kotlin.model.Bancaria
import java.util.Optional

interface IBancariaRepository : JpaRepository<Bancaria, Long> {
    fun findByBancariaId(bancariaId: Long): Optional<Bancaria?>?

}