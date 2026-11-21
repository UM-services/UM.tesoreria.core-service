package um.tesoreria.core.hexagonal.pagos.proveedorValor.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.infrastructure.persistence.entity.ProveedorValorEntity;

import java.util.List;

@Repository
public interface JpaProveedorValorRepository extends JpaRepository<ProveedorValorEntity, Long> {

    List<ProveedorValorEntity> findAllByProveedorMovimientoIdOrderByOrdenAsc(Long proveedorMovimientoId);
}
