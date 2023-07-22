package um.tesoreria.rest.repository.view;

import um.tesoreria.rest.kotlin.model.view.OrdenPagoDetalle;

import java.util.List;

public interface OrdenPagoDetalleRepositoryCustom {
    public List<OrdenPagoDetalle> findAllByStrings(List<String> conditions);

}
