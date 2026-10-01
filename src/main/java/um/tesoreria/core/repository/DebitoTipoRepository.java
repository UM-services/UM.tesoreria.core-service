package um.tesoreria.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.kotlin.model.DebitoTipo;


@Repository
public interface DebitoTipoRepository extends JpaRepository<DebitoTipo, Integer> {

    Optional<DebitoTipo> findByDebitoTipoId(Integer debitoTipoId);

}