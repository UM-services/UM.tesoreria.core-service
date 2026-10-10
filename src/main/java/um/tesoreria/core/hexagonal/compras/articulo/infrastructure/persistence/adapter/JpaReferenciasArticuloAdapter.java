package um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.adapter;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.ReferenciaArticulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ReferenciasArticuloRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Conteos sobre las columnas indexadas que guardan el id del artículo (inventario de dev, 2026-10-05):
 * {@code entrega_detalle.NeD_Art_ID} (FK {@code entrega_detalle_ibfk_2}) y {@code movprov_detallefactura.FaD_Art_ID}
 * (sin FK: sin este chequeo la base dejaría borrar y las líneas de factura quedarían huérfanas).
 */
@Component
@RequiredArgsConstructor
public class JpaReferenciasArticuloAdapter implements ReferenciasArticuloRepository {

    /** Tabla → columna con el id del artículo, en el orden en que se informan. */
    static final Map<String, String> COLUMNA_POR_TABLA = new LinkedHashMap<>();

    static {
        COLUMNA_POR_TABLA.put("entrega_detalle", "NeD_Art_ID");
        COLUMNA_POR_TABLA.put("movprov_detallefactura", "FaD_Art_ID");
    }

    private final EntityManager entityManager;

    @Override
    public List<ReferenciaArticulo> findReferencias(Long articuloId) {
        var referencias = new ArrayList<ReferenciaArticulo>();
        for (var tablaYColumna : COLUMNA_POR_TABLA.entrySet()) {
            var tabla = tablaYColumna.getKey();
            var cantidad = ((Number) entityManager
                    .createNativeQuery("SELECT COUNT(*) FROM " + tabla + " WHERE " + tablaYColumna.getValue() + " = :id")
                    .setParameter("id", articuloId)
                    .getSingleResult()).longValue();
            if (cantidad > 0) {
                referencias.add(new ReferenciaArticulo(tabla, cantidad));
            }
        }
        return referencias;
    }
}
