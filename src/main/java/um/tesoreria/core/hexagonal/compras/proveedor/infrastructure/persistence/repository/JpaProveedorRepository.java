package um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.entity.ProveedorEntity;
import java.util.Optional;

@Repository
public interface JpaProveedorRepository extends JpaRepository<ProveedorEntity, Integer> {
    Optional<ProveedorEntity> findByProveedorId(Integer proveedorId);
    Optional<ProveedorEntity> findFirstByCuitOrderByProveedorIdAsc(String cuit);

    boolean existsByCuitAndProveedorIdNot(String cuit, Integer proveedorId);

    boolean existsByCuit(String cuit);
    Optional<ProveedorEntity> findTopByOrderByProveedorIdDesc();
}