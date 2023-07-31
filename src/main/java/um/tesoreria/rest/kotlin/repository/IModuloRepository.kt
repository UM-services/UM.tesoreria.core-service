package um.tesoreria.rest.kotlin.repository;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import um.tesoreria.rest.kotlin.model.Modulo

@Repository
interface IModuloRepository : JpaRepository<Modulo, Int> {

}