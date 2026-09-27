package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.persistence.entity.UsuarioChequeraGeograficaEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUsuarioChequeraGeograficaRepository extends JpaRepository<UsuarioChequeraGeograficaEntity, Long> {

    List<UsuarioChequeraGeograficaEntity> findAllByUserId(Long userId);

    Optional<UsuarioChequeraGeograficaEntity> findByUserIdAndGeograficaId(Long userId, Integer geograficaId);

    void deleteByUserIdAndGeograficaId(Long userId, Integer geograficaId);

}
