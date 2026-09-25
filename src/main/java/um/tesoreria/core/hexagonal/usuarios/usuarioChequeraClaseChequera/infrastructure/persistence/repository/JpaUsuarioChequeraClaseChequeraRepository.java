package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.entity.UsuarioChequeraClaseChequeraEntity;

import java.util.List;

@Repository
public interface JpaUsuarioChequeraClaseChequeraRepository extends JpaRepository<UsuarioChequeraClaseChequeraEntity, Long> {

    List<UsuarioChequeraClaseChequeraEntity> findAllByUserId(Long userId);

}
