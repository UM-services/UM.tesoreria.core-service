package um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.entity.FacturaDetalleReferenciaEntity;

@Repository
public interface JpaFacturaDetalleReferenciaRepository extends JpaRepository<FacturaDetalleReferenciaEntity, Long> {
    long countByArticuloId(Long articuloId);
}
