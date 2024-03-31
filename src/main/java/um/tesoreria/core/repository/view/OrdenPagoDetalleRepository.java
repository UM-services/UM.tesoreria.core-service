package um.tesoreria.core.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.view.OrdenPagoDetalle;

@Repository
public interface OrdenPagoDetalleRepository extends JpaRepository<OrdenPagoDetalle, String>, OrdenPagoDetalleRepositoryCustom {

}