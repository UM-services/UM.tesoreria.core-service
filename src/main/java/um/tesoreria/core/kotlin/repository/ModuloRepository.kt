package um.tesoreria.core.kotlin.repository;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import um.tesoreria.core.kotlin.model.Modulo

@Repository
interface ModuloRepository : JpaRepository<Modulo, Int> {

}