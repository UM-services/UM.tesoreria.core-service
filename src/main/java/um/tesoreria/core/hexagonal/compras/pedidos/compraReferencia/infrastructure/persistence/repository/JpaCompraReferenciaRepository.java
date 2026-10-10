package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.persistence.entity.CompraReferenciaEntity;

import java.util.Optional;

@Repository
public interface JpaCompraReferenciaRepository extends JpaRepository<CompraReferenciaEntity, Integer> {

    Optional<CompraReferenciaEntity> findByEjercicioId(Integer ejercicioId);

}
