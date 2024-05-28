package um.tesoreria.core.kotlin.repository

import org.springframework.data.jpa.repository.JpaRepository
import um.tesoreria.core.kotlin.model.FacultadGrupo

interface FacultadGrupoRepository : JpaRepository<FacultadGrupo, Long> {

    fun findAllByGrupo(grupo: Int): List<FacultadGrupo>

}