package um.tesoreria.core.hexagonal.contable.asientoIngreso.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.producto.application.service.ProductoService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.dependencias.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.dependencias.geografica.application.service.GeograficaService;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;
import um.tesoreria.core.kotlin.model.TipoPago;
import um.tesoreria.core.service.TipoPagoService;

/**
 * Resuelve los nombres que el legacy mostraba en la grilla de faltantes y en el combo de tipos de
 * pago. Es tolerante: si un catálogo no encuentra el id devuelve {@code null} y la fila se muestra
 * con el id, sin romper el asiento.
 */
@Component
@RequiredArgsConstructor
public class AsientoIngresoCatalogo {

    public record TipoChequeraDatos(String nombre, Integer geograficaId) {
    }

    private final FacultadService facultadService;
    private final LectivoService lectivoService;
    private final TipoChequeraService tipoChequeraService;
    private final ProductoService productoService;
    private final GeograficaService geograficaService;
    private final TipoPagoService tipoPagoService;

    public String facultadNombre(Integer facultadId) {
        return facultadId == null ? null : seguro(() -> facultadService.findByFacultadId(facultadId).getNombre());
    }

    public String lectivoNombre(Integer lectivoId) {
        return lectivoId == null || lectivoId == 0 ? null : seguro(() -> lectivoService.findByLectivoId(lectivoId).getNombre());
    }

    public TipoChequeraDatos tipoChequera(Integer tipoChequeraId) {
        if (tipoChequeraId == null) {
            return null;
        }
        return seguro(() -> {
            var tipo = tipoChequeraService.findByTipoChequeraId(tipoChequeraId);
            return new TipoChequeraDatos(tipo.getNombre(), tipo.getGeograficaId());
        });
    }

    public String productoNombre(Integer productoId) {
        return productoId == null ? null : seguro(() -> productoService.findByProductoId(productoId).getNombre());
    }

    public String geograficaNombre(Integer geograficaId) {
        if (geograficaId == null) {
            return null;
        }
        return geograficaService.findByGeograficaId(geograficaId).map(g -> g.getNombre()).orElse(null);
    }

    public String tipoPagoNombre(Integer tipoPagoId) {
        if (tipoPagoId == null) {
            return null;
        }
        return tipoPagoService.findAll().stream()
                .filter(tipo -> tipoPagoId.equals(tipo.getTipoPagoId()))
                .map(TipoPago::getNombre)
                .findFirst()
                .orElse(null);
    }

    private static <T> T seguro(java.util.function.Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (RuntimeException e) {
            return null;
        }
    }

}
