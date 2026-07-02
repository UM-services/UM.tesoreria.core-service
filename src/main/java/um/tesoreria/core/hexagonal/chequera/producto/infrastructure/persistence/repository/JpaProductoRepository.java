package um.tesoreria.core.hexagonal.chequera.producto.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.chequera.producto.infrastructure.persistence.entity.ProductoEntity;

@Repository
public interface JpaProductoRepository extends JpaRepository<ProductoEntity, Integer> {

    Optional<ProductoEntity> findByProductoId(Integer productoId);

}
