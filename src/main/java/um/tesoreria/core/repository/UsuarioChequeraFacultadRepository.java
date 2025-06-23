package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import um.tesoreria.core.model.UsuarioChequeraFacultad;

import java.util.List;

public interface UsuarioChequeraFacultadRepository extends JpaRepository<UsuarioChequeraFacultad, Long> {

    List<UsuarioChequeraFacultad> findAllByUserId(Long userId);

}
