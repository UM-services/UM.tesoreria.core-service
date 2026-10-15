package um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.ReferenciaArticulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ReferenciasArticuloRepository;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.repository.JpaEntregaDetalleReferenciaRepository;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.repository.JpaFacturaDetalleReferenciaRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Conteos sobre las columnas indexadas que guardan el id del artículo (inventario de dev, 2026-10-05):
 * {@code entrega_detalle.NeD_Art_ID} (FK {@code entrega_detalle_ibfk_2}) y {@code movprov_detallefactura.FaD_Art_ID}
 * (sin FK: sin este chequeo la base dejaría borrar y las líneas de factura quedarían huérfanas).
 */
@Component
@RequiredArgsConstructor
public class JpaReferenciasArticuloAdapter implements ReferenciasArticuloRepository {

    private final JpaEntregaDetalleReferenciaRepository entregas;
    private final JpaFacturaDetalleReferenciaRepository facturas;

    /** Se informan en este orden: entregas, facturas. */
    @Override
    public List<ReferenciaArticulo> findReferencias(Long articuloId) {
        var referencias = new ArrayList<ReferenciaArticulo>();
        agregar(referencias, "entrega_detalle", entregas.countByArticuloId(articuloId));
        agregar(referencias, "movprov_detallefactura", facturas.countByArticuloId(articuloId));
        return referencias;
    }

    private static void agregar(List<ReferenciaArticulo> referencias, String tabla, long cantidad) {
        if (cantidad > 0) {
            referencias.add(new ReferenciaArticulo(tabla, cantidad));
        }
    }
}
