package um.tesoreria.core.hexagonal.compras.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloConflictException;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in.DeleteArticuloUseCase;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ArticuloRepository;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ReferenciasArticuloRepository;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloConflictException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in.DeleteUbicacionArticulosByArticuloUseCase;

/**
 * Baja segura: solo si ninguna tabla de negocio usa el artículo (la comprobación es explícita porque
 * {@code movprov_detallefactura} no tiene FK). Sus vínculos {@code ubicacion_articulo} se borran con él.
 * Orden de bloqueo: artículo y después vínculos. Con el artículo bloqueado, una entrega o un vínculo nuevos
 * esperan (su FK lo lee) y al confirmar la baja fallan; una línea de factura nueva no espera (sin FK).
 */
@Component
@RequiredArgsConstructor
public class DeleteArticuloUseCaseImpl implements DeleteArticuloUseCase {
    private final ArticuloRepository repository;
    private final ReferenciasArticuloRepository referenciasRepository;
    // Excepción cross-slice autorizada: los vínculos los borra su slice, con su puerto público
    // (patrón de CreateUsuarioChequeraFacultadUseCaseImpl).
    private final DeleteUbicacionArticulosByArticuloUseCase deleteUbicacionArticulosByArticuloUseCase;

    @Override
    @Transactional
    public void deleteArticulo(Long id) {
        repository.findByIdForUpdate(id).orElseThrow(() -> new ArticuloException(id));
        var referencias = referenciasRepository.findReferencias(id);
        if (!referencias.isEmpty()) {
            throw ArticuloConflictException.referenciado(id, referencias);
        }
        try {
            deleteUbicacionArticulosByArticuloUseCase.deleteByArticuloId(id);
        } catch (UbicacionArticuloConflictException ex) {
            // Solo un bloqueo puede frenar el borrado de vínculos; ArticuloService reintenta el interbloqueo
            throw ArticuloConflictException.bloqueado(id, ex.isReintentable());
        }
        repository.deleteById(id);
    }
}
