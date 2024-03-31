package um.tesoreria.core.kotlin.repository;

import org.springframework.data.jpa.repository.JpaRepository
import um.tesoreria.core.kotlin.model.Bancaria
import java.util.Optional

interface IBancariaRepository : JpaRepository<Bancaria, Long> {
    fun findByBancariaId(bancariaId: Long): Optional<Bancaria?>?

}