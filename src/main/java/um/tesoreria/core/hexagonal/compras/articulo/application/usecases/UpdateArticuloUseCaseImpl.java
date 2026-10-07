package um.tesoreria.core.hexagonal.compras.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in.UpdateArticuloUseCase;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ArticuloRepository;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.GetCuentaByNumeroCuentaUseCase;

@Component
@RequiredArgsConstructor
public class UpdateArticuloUseCaseImpl implements UpdateArticuloUseCase {
    private final ArticuloRepository repository;
    // Excepción cross-slice autorizada: valida la cuenta con el puerto público de contable.cuenta
    // (patrón de CreateUsuarioChequeraFacultadUseCaseImpl; Articulo ya ancla Cuenta en su dominio).
    private final GetCuentaByNumeroCuentaUseCase getCuentaByNumeroCuentaUseCase;
    @Override
    @Transactional
    public Articulo updateArticulo(Long id, Articulo cambios) {
        ArticuloReglas.validarCambios(cambios);
        ArticuloReglas.normalizarNumeros(cambios);
        // Antes del bloqueo: si la cuenta no existe no hace falta tomar el artículo
        ArticuloReglas.validarCuentaExistente(cambios, getCuentaByNumeroCuentaUseCase);
        // Bloqueada antes de leer el estado actual: dos ediciones del core se serializan
        Articulo actual = repository.findByIdForUpdate(id).orElseThrow(() -> new ArticuloException(id));
        return repository.update(actual.conCambios(cambios));
    }
}
