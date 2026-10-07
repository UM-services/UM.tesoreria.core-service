package um.tesoreria.core.hexagonal.compras.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in.CreateArticuloUseCase;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ArticuloRepository;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.GetCuentaByNumeroCuentaUseCase;

@Component
@RequiredArgsConstructor
public class CreateArticuloUseCaseImpl implements CreateArticuloUseCase {
    private final ArticuloRepository repository;
    // Excepción cross-slice autorizada: valida la cuenta con el puerto público de contable.cuenta
    // (patrón de CreateUsuarioChequeraFacultadUseCaseImpl; Articulo ya ancla Cuenta en su dominio).
    private final GetCuentaByNumeroCuentaUseCase getCuentaByNumeroCuentaUseCase;
    @Override
    @Transactional
    public Articulo createArticulo(Articulo articulo) {
        ArticuloReglas.validarAlta(articulo);
        ArticuloReglas.normalizarNumeros(articulo);
        ArticuloReglas.validarCuentaExistente(articulo, getCuentaByNumeroCuentaUseCase);
        return repository.create(articulo);
    }
}
