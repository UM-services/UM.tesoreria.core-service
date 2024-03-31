package um.tesoreria.core.repository.view;

import um.tesoreria.core.kotlin.model.view.OrdenPagoDetalle;

import java.util.List;

public interface OrdenPagoDetalleRepositoryCustom {
    public List<OrdenPagoDetalle> findAllByStrings(List<String> conditions);

}
