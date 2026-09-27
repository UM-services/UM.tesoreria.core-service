package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.persistence.entity.UsuarioChequeraFacultadEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUsuarioChequeraFacultadRepository extends JpaRepository<UsuarioChequeraFacultadEntity, Long> {

    List<UsuarioChequeraFacultadEntity> findAllByUserId(Long userId);

    Optional<UsuarioChequeraFacultadEntity> findByUserIdAndFacultadId(Long userId, Integer facultadId);

    void deleteByUserIdAndFacultadId(Long userId, Integer facultadId);

}
