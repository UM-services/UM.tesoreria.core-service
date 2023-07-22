package um.tesoreria.rest.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.rest.kotlin.model.view.OrdenPagoDetalle;

@Repository
public interface OrdenPagoDetalleRepository extends JpaRepository<OrdenPagoDetalle, String>, OrdenPagoDetalleRepositoryCustom {

}