package ar.edu.um.tesoreria.rest.kotlin.repository;

import ar.edu.um.tesoreria.rest.kotlin.model.Bancaria
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface IBancariaRepository : JpaRepository<Bancaria, Long> {
    fun findByBancariaId(bancariaId: Long): Optional<Bancaria?>?

}