package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.entity.UsuarioChequeraClaseChequeraEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUsuarioChequeraClaseChequeraRepository extends JpaRepository<UsuarioChequeraClaseChequeraEntity, Long> {

    List<UsuarioChequeraClaseChequeraEntity> findAllByUserId(Long userId);

    Optional<UsuarioChequeraClaseChequeraEntity> findByUserIdAndClaseChequeraId(Long userId, Integer claseChequeraId);

    void deleteByUserIdAndClaseChequeraId(Long userId, Integer claseChequeraId);

}
