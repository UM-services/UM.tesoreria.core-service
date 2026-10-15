package um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.entity.EntregaDetalleReferenciaEntity;

@Repository
public interface JpaEntregaDetalleReferenciaRepository extends JpaRepository<EntregaDetalleReferenciaEntity, Long> {
    long countByArticuloId(Long articuloId);
}
